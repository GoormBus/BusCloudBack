package goorm.domain.buslog.application.service;


import goorm.domain.buslog.domain.entity.BusLog;
import goorm.domain.member.domain.entity.Member;
import goorm.domain.member.domain.repository.MemberRepository;
import goorm.domain.buslog.presentation.dto.request.NoteRequest;
import goorm.domain.buslog.presentation.dto.response.NoteResponse;
import goorm.domain.buslog.domain.repository.NoteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoteService {

    private final NoteRepository noteRepository;

    private final MemberRepository memberRepository;

    private final StationService stationService;

    public SingleResult<BusLog> save(NoteRequest req, String userId){

        BusLog newRecord= BusLog.builder()
                .departure(req.departure())
                .destination(req.destination())
                .station(req.station())
                .time(req.time())
                .alarm(true)
                .notionId(req.notionId())
                .stationId(req.stationId())
                .favorite(false)
                .favorite_pre(1)
                .build();

        Optional<Member> findMember = memberRepository.findByPhone2(userId);
        if (findMember.isPresent()) {
            Member member = findMember.get();
            newRecord.setMember(member);
            // member에 대한 로직 처리
        } else {
            // 값이 없을 때의 처리 로직
            throw new NoSuchElementException("해당하는 사용자가 없습니다.");
        }
        BusLog save = noteRepository.save(newRecord);
        return ResponseService.getSingleResult(save);
    }
    public ListResult<NoteResponse> findAll(String memberId) {
        List<BusLog> busLogs = noteRepository.findAll(memberId);
        List<NoteResponse> list = busLogs.stream().map(NoteResponse::of).toList();
        return ResponseService.getListResult(list);
    }


    // 매일 자정에 실행 최대 빈도수 1씩 증가
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void incrementMaxForNotesWithAlarm() {
        List<BusLog> notesWithAlarm = noteRepository.findByAlarmTrue(); // alarm이 true인 BusLog 조회

        for (BusLog busLog : notesWithAlarm) {
            busLog.setFrequency(busLog.getFrequency() + 1);  // max 값 1 증가
            busLog.setExecute(false);
           if(busLog.getFavorite_pre()==1){
               noteRepository.deletePre(busLog);
           }
        }

        // 별도 업데이트 안해도 될듯
    }

    @Scheduled(cron = "0 * * * * ?") // 1분마다 실행
    @Transactional
    public void alarmEveryDay() {
        // alarm이 true인 BusLog 조회
        List<BusLog> notesWithAlarm = noteRepository.findByAlarmTrueisExcute();
        LocalTime now = LocalTime.now(); // 현재 시간
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        for (BusLog busLog : notesWithAlarm) {
            if (busLog.getTime() != null) { // note의 time이 null이 아닌 경우에만 진행
                LocalTime noteTime = LocalTime.parse(busLog.getTime(), formatter); // String을 LocalTime으로 변환

                // 현재 시간이 noteTime 이후라면 stationService 메서드 호출
                if (now.isAfter(noteTime)) {
                    stationService.scheduleBusApiCall(
                            busLog.getMember().getMemberId(),
                            busLog.getNotionId(),
                            busLog.getStationId(),
                            busLog.getStation()
                    );
                }

                // 실행된 Note의 executed 필드를 true로 설정
                busLog.setExecute(true);
            }
        }
    }
}

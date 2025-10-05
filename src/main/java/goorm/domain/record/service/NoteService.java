package goorm.domain.record.service;


import goorm.domain.member.domain.entity.Member;
import goorm.domain.member.domain.repository.MemberRepository;
import goorm.domain.record.dto.request.NoteRequest;
import goorm.domain.record.dto.response.NoteResponse;
import goorm.domain.record.entity.Note;
import goorm.domain.record.repository.NoteRepository;
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

    public SingleResult<Note> save(NoteRequest req,String userId){

        Note newRecord= Note.builder()
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
        Note save = noteRepository.save(newRecord);
        return ResponseService.getSingleResult(save);
    }
    public ListResult<NoteResponse> findAll(String memberId) {
        List<Note> notes = noteRepository.findAll(memberId);
        List<NoteResponse> list = notes.stream().map(NoteResponse::of).toList();
        return ResponseService.getListResult(list);
    }


    // 매일 자정에 실행 최대 빈도수 1씩 증가
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void incrementMaxForNotesWithAlarm() {
        List<Note> notesWithAlarm = noteRepository.findByAlarmTrue(); // alarm이 true인 Note 조회

        for (Note note : notesWithAlarm) {
            note.setFrequency(note.getFrequency() + 1);  // max 값 1 증가
            note.setExecute(false);
           if(note.getFavorite_pre()==1){
               noteRepository.deletePre(note);
           }
        }

        // 별도 업데이트 안해도 될듯
    }

    @Scheduled(cron = "0 * * * * ?") // 1분마다 실행
    @Transactional
    public void alarmEveryDay() {
        // alarm이 true인 Note 조회
        List<Note> notesWithAlarm = noteRepository.findByAlarmTrueisExcute();
        LocalTime now = LocalTime.now(); // 현재 시간
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        for (Note note : notesWithAlarm) {
            if (note.getTime() != null) { // note의 time이 null이 아닌 경우에만 진행
                LocalTime noteTime = LocalTime.parse(note.getTime(), formatter); // String을 LocalTime으로 변환

                // 현재 시간이 noteTime 이후라면 stationService 메서드 호출
                if (now.isAfter(noteTime)) {
                    stationService.scheduleBusApiCall(
                            note.getMember().getMemberId(),
                            note.getNotionId(),
                            note.getStationId(),
                            note.getStation()
                    );
                }

                // 실행된 Note의 executed 필드를 true로 설정
                note.setExecute(true);
            }
        }
    }
}

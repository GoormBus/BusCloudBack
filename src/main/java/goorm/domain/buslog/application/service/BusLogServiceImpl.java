package goorm.domain.buslog.application.service;


import goorm.domain.busalarm.domain.entity.BusAlarm;
import goorm.domain.busalarm.domain.repository.BusAlarmRepository;
import goorm.domain.buslog.domain.entity.BusFavorite;
import goorm.domain.buslog.domain.entity.BusLog;
import goorm.domain.buslog.domain.repository.BusFavoriteRepository;
import goorm.domain.buslog.presentation.dto.request.BusFavoriteReq;
import goorm.domain.member.domain.entity.Member;
import goorm.domain.member.domain.repository.MemberRepository;
import goorm.domain.buslog.presentation.dto.request.BusLogSaveReq;
import goorm.domain.buslog.presentation.dto.response.BusLogAllRes;
import goorm.domain.buslog.domain.repository.BusLogRepository;
import goorm.global.infra.exception.error.ErrorCode;
import goorm.global.infra.exception.error.GoormBusException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BusLogServiceImpl {

    private final BusLogRepository busLogRepository;
    private final BusFavoriteRepository busFavoriteRepository;
    private final BusAlarmRepository busAlarmRepository;

    private final MemberRepository memberRepository;

    private final StationService stationService;

    public void postBusLogSave(BusLogSaveReq req, String userId){

        Member findMember = memberRepository.findById(userId).orElse(null);
        if(findMember == null) throw new GoormBusException(ErrorCode.USER_NOT_EXIST);

        BusLog newBusLog= BusLog.builder()
                .member(findMember)
                .departure(req.departure())
                .destination(req.destination())
                .station(req.station())
                .notionId(req.notionId())
                .stationId(req.stationId())
                .build();

        busLogRepository.save(newBusLog);
    }
    public List<BusLogAllRes> getBusLogAll(String memberId) {
        Member findMember = memberRepository.findById(memberId).orElse(null);
        if(findMember == null) throw new GoormBusException(ErrorCode.USER_NOT_EXIST);

        List<BusLogAllRes> result = new ArrayList<>();
        List<BusLog> busLogs = busLogRepository.findByMember(findMember);

        for (BusLog busLog : busLogs) {
            BusAlarm findBusAlarm = busAlarmRepository.findByBusLog(busLog).orElse(null);
            if(findBusAlarm==null) throw new GoormBusException(ErrorCode.BUS_ALARM_NOT_EXIST);

            BusFavorite findBusFavorite = busFavoriteRepository.findByBusLog(busLog).orElse(null);
            if(findBusFavorite==null) throw new GoormBusException(ErrorCode.BUS_FAVORITE_NOT_EXIST);

            result.add(BusLogAllRes.of(busLog, findBusAlarm.isAlarmFlag(), findBusFavorite.isFavoriteFlag()));
        }

        return result;
    }

    public void updateBusFavorite(BusFavoriteReq req){
        BusLog findBusLog = busLogRepository.findById(req.busLogId()).orElse(null);
        if(findBusLog == null) throw new GoormBusException(ErrorCode.BUS_LOG_NOT_EXIST);

        BusFavorite findBusFavorite = busFavoriteRepository.findByBusLog(findBusLog).orElse(null);
        if(findBusFavorite==null) throw new GoormBusException(ErrorCode.BUS_FAVORITE_NOT_EXIST);

        if(findBusFavorite.isFavoriteFlag()) findBusFavorite.deactivateIsFavoriteFlag();
        else findBusFavorite.activateIsFavoriteFlag();

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

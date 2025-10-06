package goorm.global.infra.scheduled;


import goorm.domain.busalarm.domain.entity.BusAlarm;
import goorm.domain.busalarm.domain.repository.BusAlarmRepository;
import goorm.domain.buslog.domain.entity.BusLog;
import goorm.domain.buslog.domain.repository.BusLogRepository;
import goorm.domain.member.domain.entity.Member;
import goorm.domain.member.domain.repository.MemberRepository;
import goorm.global.infra.exception.error.ErrorCode;
import goorm.global.infra.exception.error.GoormBusException;
import goorm.global.infra.feignclient.JejuBusClient;
import goorm.global.infra.feignclient.dto.ArrivalResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class StationService {
    private final BusLogRepository busLogRepository;
    private final JejuBusClient jejuBusClient;
    private final BusAlarmRepository busAlarmRepository;


    // 30초마다 실행되는 메서드 - 사용자별로 독립적으로 동작
    @Scheduled(fixedRate = 30000)
    public void callBusScheduler() {
        List<BusLog> findBusLogAll = busLogRepository.findAll();
        for (BusLog busLog : findBusLogAll) {
            BusAlarm findBusAlarm = busAlarmRepository.findByBusLog(busLog).orElse(null);
            if (findBusAlarm == null) throw new GoormBusException(ErrorCode.BUS_ALARM_NOT_EXIST);

            // 비활성화 일떄 건너뛰기
            if (!findBusAlarm.isAlarmFlag()) continue;

            // 잔여 횟수가 0일떄 건너뛰기
            if (findBusAlarm.getAlarmRemaining() == 0L) continue;


            checkBusArrivalAndNotify(busLog, findBusAlarm);
        }

    }

    private void checkBusArrivalAndNotify(BusLog busLog, BusAlarm busAlarm) {
        ArrivalResponse response = jejuBusClient.getArrivalInfo(busLog.getStationId());
        if (response == null || response.getResultList() == null) {
            throw new GoormBusException(ErrorCode.JEJU_RESPONSE_NOT_EXIST);
        }


        response.getResultList().forEach(arrival -> {
            int remainStation = Integer.parseInt(arrival.getRemainStation());
            int busLogStation = Integer.parseInt(String.valueOf(busLog.getStation()));

            // 잔여랑 api response 응답값이랑 똑같을때 알림콜 호출
            if (remainStation == busLogStation) {
                busAlarm.updateAlarmRemaining(); // -1 씩 감소

            }
        });
    }


}

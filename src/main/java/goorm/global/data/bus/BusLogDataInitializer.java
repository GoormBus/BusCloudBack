package goorm.global.data.bus;

import goorm.domain.busalarm.domain.entity.BusAlarm;
import goorm.domain.busalarm.domain.repository.BusAlarmRepository;
import goorm.domain.buslog.application.service.BusLogService;
import goorm.domain.buslog.domain.entity.BusFavorite;
import goorm.domain.buslog.domain.entity.BusLog;
import goorm.domain.buslog.domain.repository.BusFavoriteRepository;
import goorm.domain.buslog.domain.repository.BusLogRepository;
import goorm.domain.member.domain.entity.Member;
import goorm.domain.member.domain.repository.MemberRepository;
import goorm.global.infra.exception.error.ErrorCode;
import goorm.global.infra.exception.error.GoormBusException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

//@Component
@RequiredArgsConstructor
@Slf4j
public class BusLogDataInitializer {
    private final BusLogRepository busLogRepository;
    private final BusFavoriteRepository busFavoriteRepository;
    private final BusAlarmRepository busAlarmRepository;
    private final MemberRepository memberRepository;

    @PostConstruct
    @Transactional
    public void initializeBusLogs() {
        Member findMember = memberRepository.findById("6bb60da0-f7bf-4da0-80c2-249d7cd21973")
                .orElseThrow(() -> new GoormBusException(ErrorCode.USER_NOT_EXIST));
        log.info("🚍 BusLog 데이터 초기화 시작");
        for (int i = 0; i < 10000; i++) {

            // req 대신 랜덤 또는 i 기반으로 값 생성
            String departure = "출발지" + i;
            String destination = "도착지" + i;
            Long station = (long) i;
            String notionId = UUID.randomUUID().toString();
            String stationId = String.valueOf(i);

            BusLog busLog = BusLog.builder()
                    .member(findMember)
                    .departure(departure)
                    .destination(destination)
                    .station(station)
                    .notionId(notionId)
                    .stationId(stationId)
                    .build();

            BusFavorite busFavorite = BusFavorite.builder()
                    .busLog(busLog)
                    .build();

            BusAlarm busAlarm = BusAlarm.builder()
                    .busLog(busLog)
                    .build();

            busLogRepository.save(busLog);
            busFavoriteRepository.save(busFavorite);
            busAlarmRepository.save(busAlarm);

            if (i % 1000 == 0) {
                log.info("✅ {}개 데이터 삽입 완료", i);
            }
        }

    }

}

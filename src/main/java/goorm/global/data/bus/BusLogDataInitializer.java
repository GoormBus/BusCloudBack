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

@Component
@RequiredArgsConstructor
@Slf4j
public class BusLogDataInitializer {
    private final BusLogRepository busLogRepository;
    private final BusFavoriteRepository busFavoriteRepository;
    private final BusAlarmRepository busAlarmRepository;
    private final MemberRepository memberRepository;

    //@PostConstruct
    //@Transactional
    public void initializeBusLogsV1() {
        Member findMember = memberRepository.findById("ea45ebca-ce2a-4abe-8b8c-470699637be5")
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

            if (i % 100 == 0) {
                log.info("✅ {}개 데이터 삽입 완료", i);
            }
        }

    }

    @PostConstruct
    @Transactional
    public void initializeBusLogsV2() {
        log.info("🚀 더미 데이터 초기화 시작");

        int totalMembers = 50;
        int logsPerMember = 50;

        for (int m = 1; m <= totalMembers; m++) {
            // 회원 생성
            Member member = Member.builder()
                    .phone("010-0000-" + String.format("%04d", m))
                    .name("테스트유저" + m)
                    .build();
            memberRepository.save(member);

            // 각 회원당 bus_log 50개 생성
            for (int i = 0; i < logsPerMember; i++) {
                String departure = "출발지_" + m + "_" + i;
                String destination = "도착지_" + m + "_" + i;
                Long station = (long) ((m * 100) + i);
                String notionId = UUID.randomUUID().toString();
                String stationId = String.valueOf((m * 1000) + i);

                BusLog busLog = BusLog.builder()
                        .member(member)
                        .departure(departure)
                        .destination(destination)
                        .station(station)
                        .notionId(notionId)
                        .stationId(stationId)
                        .build();
                busLogRepository.save(busLog);

                BusFavorite busFavorite = BusFavorite.builder()
                        .busLog(busLog)
                        .build();
                busFavoriteRepository.save(busFavorite);

                BusAlarm busAlarm = BusAlarm.builder()
                        .busLog(busLog)
                        .build();
                busAlarmRepository.save(busAlarm);
            }

            log.info("✅ {}번 Member의 BusLog 50개 삽입 완료", m);
        }

    }

}

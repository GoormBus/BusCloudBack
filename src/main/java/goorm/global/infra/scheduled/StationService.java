package goorm.global.infra.scheduled;

import goorm.domain.busalarm.application.service.BusAlarmService;
import goorm.domain.busalarm.domain.entity.BusAlarm;
import goorm.domain.busalarm.domain.repository.BusAlarmRepository;
import goorm.domain.buslog.domain.entity.BusLog;
import goorm.domain.buslog.domain.repository.BusLogRepository;
import goorm.global.infra.exception.error.ErrorCode;
import goorm.global.infra.exception.error.GoormBusException;
import goorm.global.infra.feignclient.JejuBusClient;
import goorm.global.infra.feignclient.dto.ArrivalResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 🕒 StationService
 *
 * <p>버스 도착 정보와 알림 발송을 담당하는 스케줄링 서비스입니다.</p>
 * <ul>
 *   <li>30초마다 버스 도착 여부를 확인하고 음성 알림 발송</li>
 *   <li>24시간이 지난 알림의 잔여 횟수를 자동으로 초기화</li>
 * </ul>
 */
@Service
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class StationService {

    private final BusLogRepository busLogRepository;
    private final JejuBusClient jejuBusClient;
    private final BusAlarmRepository busAlarmRepository;
    private final BusAlarmService busAlarmService;

    /**
     * 🔁 30초마다 실행되는 버스 도착 알림 스케줄러
     *
     * <p>모든 BusLog를 순회하면서:
     * <ul>
     *     <li>알림이 비활성화된 경우 제외</li>
     *     <li>잔여 알림 횟수가 0인 경우 제외</li>
     *     <li>JejuBus API를 통해 도착 정보를 조회 후,
     *     해당 정류장에 도착한 경우 음성 알림 발송 및 잔여 횟수 차감</li>
     * </ul>
     */
    @Scheduled(fixedRate = 30000)
    public void callBusScheduler() {
        List<BusLog> findBusLogAll = busLogRepository.findAll();
        for (BusLog busLog : findBusLogAll) {
            BusAlarm findBusAlarm = busAlarmRepository.findByBusLog(busLog)
                    .orElseThrow(() -> new GoormBusException(ErrorCode.BUS_ALARM_NOT_EXIST));

            // 비활성화된 알림은 건너뛰기
            if (!findBusAlarm.isAlarmFlag()) continue;

            // 잔여 횟수가 0이면 알림 스킵
            if (findBusAlarm.getAlarmRemaining() == 0L) continue;

            checkBusArrivalAndNotify(busLog, findBusAlarm);
        }
    }

    /**
     * ⏰ 1분마다 실행되는 알림 초기화 스케줄러
     *
     * <p>버스 로그 생성 이후 24시간이 지난 경우,
     * 알림 잔여 횟수를 2로 초기화합니다.</p>
     */
    @Scheduled(fixedRate = 60000)
    public void reactivateBusNotification() {
        List<BusLog> findBusLogAll = busLogRepository.findAll();
        for (BusLog busLog : findBusLogAll) {
            BusAlarm findBusAlarm = busAlarmRepository.findByBusLog(busLog)
                    .orElseThrow(() -> new GoormBusException(ErrorCode.BUS_ALARM_NOT_EXIST));

            LocalDateTime createdAt = findBusAlarm.getCreatedAt();
            LocalDateTime now = LocalDateTime.now();

            // 24시간 이상 경과 확인
            if (Duration.between(createdAt, now).toHours() >= 24) {
                if (findBusAlarm.getAlarmRemaining() < 2) {
                    findBusAlarm.initAlarmRemaining();
                    log.info("버스 알림 잔여 횟수 초기화 완료: busLogId={}", busLog.getId());
                }
            }
        }
    }

    /**
     * 🚌 버스 도착 여부 확인 후 알림 발송
     *
     * <p>JejuBus API로 버스 도착 정보를 조회하고,
     * 현재 사용자의 목표 정류장과 일치하면 음성 알림을 발송합니다.</p>
     *
     * @param busLog  버스 운행 로그
     * @param busAlarm 해당 로그의 알림 엔티티
     * @throws GoormBusException 제주 버스 API 응답이 유효하지 않을 경우
     */
    private void checkBusArrivalAndNotify(BusLog busLog, BusAlarm busAlarm) {
        ArrivalResponse response = jejuBusClient.getArrivalInfo(busLog.getStationId());
        if (response == null || response.getResultList() == null)
            throw new GoormBusException(ErrorCode.JEJU_RESPONSE_NOT_EXIST);

        response.getResultList().forEach(arrival -> {
            int remainStation = Integer.parseInt(arrival.getRemainStation());
            int busLogStation = Integer.parseInt(String.valueOf(busLog.getStation()));

            // 현재 정류장 도착 시 알림 발송
            if (remainStation == busLogStation) {
                busAlarm.minusAlarmRemaining(); // 잔여 횟수 -1 감소
                busAlarmService.sendBusArrivalVoiceNotification(busLog.getMember(), busLog);
                log.info("버스 도착 알림 발송 완료: busLogId={}, member={}", busLog.getId(), busLog.getMember().getName());
            }
        });
    }
}

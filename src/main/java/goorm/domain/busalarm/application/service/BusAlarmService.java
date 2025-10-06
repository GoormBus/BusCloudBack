package goorm.domain.busalarm.application.service;

import goorm.domain.busalarm.presentation.dto.req.BusAlarmReq;
import goorm.domain.buslog.domain.entity.BusLog;
import goorm.domain.member.domain.entity.Member;

/**
 * 🔔 BusAlarmService
 *
 * <p>버스 도착 알림 관련 기능을 제공하는 서비스 인터페이스입니다.</p>
 * <ul>
 *     <li>알림 활성/비활성 토글</li>
 *     <li>Twilio를 이용한 음성 알림 발송</li>
 * </ul>
 */
public interface BusAlarmService {

    /**
     * 🚦 버스 알림 상태 변경
     *
     * <p>해당 버스 로그에 연결된 알림 상태를 토글합니다.
     * (활성화 ↔ 비활성화)</p>
     *
     * @param req 알림 상태 변경 요청 DTO
     * @throws goorm.global.infra.exception.error.GoormBusException
     *         버스 로그 또는 알림 정보가 존재하지 않을 경우
     */
    void updateBusAlarm(BusAlarmReq req);

    /**
     * 📞 버스 도착 음성 알림 발송
     *
     * <p>Twilio API를 이용하여 사용자의 전화번호로 버스 도착 안내 음성 전화를 발송합니다.</p>
     *
     * @param member 알림을 받을 사용자 엔티티
     * @param busLog 버스 운행 로그 정보
     * @throws IllegalArgumentException 전화번호 형식이 잘못된 경우
     * @throws com.twilio.exception.ApiException Twilio API 통신 오류 발생 시
     */
    void sendBusArrivalVoiceNotification(Member member, BusLog busLog);
}

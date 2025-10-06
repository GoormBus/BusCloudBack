package goorm.domain.busalarm.application.service.impl;

import com.twilio.type.PhoneNumber;
import com.twilio.type.Twiml;
import goorm.domain.busalarm.application.service.BusAlarmService;
import goorm.domain.busalarm.domain.entity.BusAlarm;
import goorm.domain.busalarm.domain.repository.BusAlarmRepository;
import goorm.domain.buslog.domain.entity.BusLog;
import goorm.domain.buslog.domain.repository.BusLogRepository;
import goorm.domain.busalarm.presentation.dto.req.BusAlarmReq;
import goorm.domain.member.domain.entity.Member;
import goorm.global.infra.exception.error.ErrorCode;
import goorm.global.infra.exception.error.GoormBusException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Say;

/**
 * 🔔 BusAlarmServiceImpl
 *
 * <p>버스 알림 관련 비즈니스 로직을 구현한 클래스입니다.</p>
 * <ul>
 *   <li>버스 알림 플래그 토글</li>
 *   <li>Twilio API를 이용한 전화 알림 발송</li>
 * </ul>
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class BusAlarmServiceImpl implements BusAlarmService {

    private final BusAlarmRepository busAlarmRepository;
    private final BusLogRepository busLogRepository;

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.from-number}")
    private String fromNumber;

    /**
     * 🚦 버스 알림 상태 토글
     *
     * <p>해당 버스 로그의 알림 상태를 활성화 ↔ 비활성화로 전환합니다.</p>
     *
     * @param req 알림 상태 변경 요청 DTO
     * @throws GoormBusException 버스 로그 또는 알림 정보가 존재하지 않을 경우
     */
    @Override
    public void updateBusAlarm(BusAlarmReq req) {
        BusLog findBusLog = busLogRepository.findById(req.busLogId()).orElse(null);
        if (findBusLog == null)
            throw new GoormBusException(ErrorCode.BUS_LOG_NOT_EXIST);

        BusAlarm findBusAlarm = busAlarmRepository.findByBusLog(findBusLog).orElse(null);
        if (findBusAlarm == null)
            throw new GoormBusException(ErrorCode.BUS_ALARM_NOT_EXIST);

        if (findBusAlarm.isAlarmFlag()) {
            findBusAlarm.deactivateIsAlarmFlag();
            log.info("버스 알림 비활성화: busLogId={}", req.busLogId());
        } else {
            findBusAlarm.activateIsAlarmFlag();
            log.info("버스 알림 활성화: busLogId={}", req.busLogId());
        }
    }

    /**
     * 📞 버스 도착 음성 알림 발송
     *
     * <p>Twilio API를 통해 버스 도착 안내 음성 전화를 사용자에게 발송합니다.</p>
     *
     * @param member 알림을 받을 사용자
     * @param busLog 버스 운행 로그
     */
    @Override
    public void sendBusArrivalVoiceNotification(Member member, BusLog busLog) {
        Twilio.init(accountSid, authToken);

        String recipientNumber = formatRecipientPhone(member.getPhone());
        String message = buildArrivalMessage(member, busLog);
        String twimlXml = buildTwimlXml(message);

        makeVoiceCall(recipientNumber, twimlXml);
        log.info("버스 도착 알림 발송 완료: member={}, phone={}", member.getName(), recipientNumber);
    }

    /**
     * ☎ 전화번호를 국제 표준 형태(+82...)로 변환
     *
     * @param phone 사용자 전화번호
     * @return 국제 표준 형태의 전화번호
     * @throws IllegalArgumentException 전화번호 형식이 잘못된 경우
     */
    private String formatRecipientPhone(String phone) {
        if (phone == null || phone.length() < 2) {
            throw new IllegalArgumentException("잘못된 전화번호 형식입니다: " + phone);
        }
        return "+82" + phone.substring(1); // 예: 010 → +8210
    }

    /**
     * 🗣 안내 음성 메시지 문자열 생성
     *
     * @param member 사용자 정보
     * @param busLog 버스 운행 로그
     * @return 음성 메시지 내용
     */
    private String buildArrivalMessage(Member member, BusLog busLog) {
        return String.format(
                "안녕하세요, %s님. %s에서 %s로 가는 %s번 버스가 현재 %s 정류장 남았습니다. 서둘러 주세요.",
                member.getName(),
                busLog.getDeparture(),
                busLog.getDestination(),
                busLog.getNotionId(),
                busLog.getStation()
        );
    }

    /**
     * 📄 Twilio용 TwiML XML 생성
     *
     * @param message 음성 안내 문장
     * @return TwiML XML 문자열
     */
    private String buildTwimlXml(String message) {
        Say say = new Say.Builder(message)
                .language(Say.Language.KO_KR)
                .voice(Say.Voice.ALICE)
                .build();

        VoiceResponse response = new VoiceResponse.Builder()
                .say(say)
                .build();

        return response.toXml();
    }

    /**
     * 📡 Twilio API를 통해 실제 전화를 발신
     *
     * @param recipientNumber 수신자 전화번호
     * @param twimlXml Twilio용 XML 메시지
     */
    private void makeVoiceCall(String recipientNumber, String twimlXml) {
        Twiml twiml = new Twiml(twimlXml);
        Call.creator(
                new PhoneNumber(recipientNumber),
                new PhoneNumber(fromNumber),
                twiml
        ).create();
    }
}

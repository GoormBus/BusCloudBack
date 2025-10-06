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

    @Override
    public void updateBusAlarm(BusAlarmReq req) {
        BusLog findBusLog = busLogRepository.findById(req.busLogId()).orElse(null);
        if (findBusLog == null) throw new GoormBusException(ErrorCode.BUS_LOG_NOT_EXIST);

        BusAlarm findBusAlarm = busAlarmRepository.findByBusLog(findBusLog).orElse(null);
        if (findBusAlarm == null) throw new GoormBusException(ErrorCode.BUS_ALARM_NOT_EXIST);

        if (findBusAlarm.isAlarmFlag()) findBusAlarm.deactivateIsAlarmFlag();
        else findBusAlarm.activateIsAlarmFlag();
    }


    /**
     * 버스 도착 음성 알림을 발송한다.
     *
     * @param member 알림 받을 사용자
     * @param busLog 버스 운행 로그
     */
    public void sendBusArrivalVoiceNotification(Member member, BusLog busLog) {
        Twilio.init(accountSid, authToken);

        String recipientNumber = formatRecipientPhone(member.getPhone());
        String message = buildArrivalMessage(member, busLog);
        String twimlXml = buildTwimlXml(message);

        makeVoiceCall(recipientNumber, twimlXml);
    }

    /**
     * 수신자 전화번호를 국제 표준 형태(+82...)로 변환한다.
     */
    private String formatRecipientPhone(String phone) {
        if (phone == null || phone.length() < 2) {
            throw new IllegalArgumentException("잘못된 전화번호 형식입니다: " + phone);
        }
        return "+82" + phone.substring(1); // 예: 010 → +8210
    }

    /**
     * 안내 음성 메시지를 생성한다.
     */
    private String buildArrivalMessage(Member member, BusLog busLog) {
        return String.format(
                "안녕하세요, %s님. %s에서 %s로 가는 %s번 버스가 현재 %s정류장 남았습니다. 서둘러 주세요.",
                member.getName(),
                busLog.getDeparture(),
                busLog.getDestination(),
                busLog.getNotionId(),
                busLog.getStation()
        );
    }

    /**
     * Twilio용 TwiML XML을 생성한다.
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
     * Twilio API를 통해 실제 전화를 건다.
     */
    private void makeVoiceCall(String recipientNumber, String twimlXml) {
        Twiml twiml = new Twiml(twimlXml);
        Call call = Call.creator(
                new PhoneNumber(recipientNumber),
                new PhoneNumber(fromNumber),
                twiml
        ).create();

    }
}

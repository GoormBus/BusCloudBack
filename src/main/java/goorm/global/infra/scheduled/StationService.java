package goorm.global.infra.scheduled;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Say;
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
    private final MemberRepository memberRepository;
    private final BusLogRepository busLogRepository;
    private final JejuBusClient jejuBusClient;
    private final BusAlarmRepository busAlarmRepository;

    private String accountSid = "AC6daasdf7c0729c";
    private String authToken = "4d";


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


            checkBusArrivalAndNotify(busLog);
        }

    }

    private void checkBusArrivalAndNotify(BusLog busLog) {
        ArrivalResponse response = jejuBusClient.getArrivalInfo(busLog.getStationId());
        if (response == null || response.getResultList() == null) {
            throw new GoormBusException(ErrorCode.JEJU_RESPONSE_NOT_EXIST);
        }


        response.getResultList().forEach(arrival -> {
            int remainStation = Integer.parseInt(arrival.getRemainStation());
            int busLogStation = Integer.parseInt(String.valueOf(busLog.getStation()));

            // 잔여랑 api response 응답값이랑 똑같을때 알림콜 호출
            if (remainStation == busLogStation) {

            }
        });
    }

    private void bus_call(Member member) {
        Twilio.init(accountSid, authToken);
        log.info("버스 콜 실행");
        String phone = member.getPhone();
        String substring = phone.substring(1);
        String from = "+16232992975";
        String to = "+82" + substring;
        log.info(to);

        // 사용자에게 전달할 음성 메시지 작성
        Say say = new Say.Builder("안녕하세요, 이것은 당신을 위한 음성 메시지입니다.")
                .language(Say.Language.KO_KR)
                .voice(Say.Voice.ALICE)
                .build();

        VoiceResponse response = new VoiceResponse.Builder()
                .say(say)
                .build();

        // VoiceResponse를 XML 문자열로 변환
        String twiml = response.toXml();

        // TwiML XML 문자열을 Twiml 객체로 감싸기
        com.twilio.type.Twiml twimlObject = new com.twilio.type.Twiml(twiml);

        // Twiml 객체를 사용하여 전화 걸기
        Call call = Call.creator(
                new com.twilio.type.PhoneNumber(to),
                new com.twilio.type.PhoneNumber(from),
                twimlObject
        ).create();

        log.info("Twilio Call SID: {}", call.getSid());
    }


}

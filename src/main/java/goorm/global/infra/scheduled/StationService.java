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
    private final BusAlarmRepository busAlarmRepository;

    private String accountSid="AC6daasdf7c0729c";
    private String authToken="4d";


    // 30초마다 실행되는 메서드 - 사용자별로 독립적으로 동작
    @Scheduled(fixedRate = 30000)
    public void callBusScheduler() {
        List<BusLog> findBusLogAll = busLogRepository.findAll();
        for (BusLog busLog : findBusLogAll) {
            BusAlarm findBusAlarm = busAlarmRepository.findByBusLog(busLog).orElse(null);
            if(findBusAlarm==null) throw new GoormBusException(ErrorCode.BUS_ALARM_NOT_EXIST);

            // 비활성화 일떄 건너뛰기
            if(!findBusAlarm.isAlarmFlag()) continue;

            // 잔여 횟수가 0일떄 건너뛰기
            if(findBusAlarm.getAlarmRemaining() == 0L) continue;

        }
        userStationIdMap.forEach((userId, stationId) -> {
            String busId = userBusIdMap.get(userId);
            Integer station = userStationMap.get(userId);
            Boolean stopCalling = userStopCallingMap.getOrDefault(userId, true);
            AtomicInteger cnt = userCntMap.getOrDefault(userId, new AtomicInteger(0));
            Set<Integer> seenBuses = userSeenBusesMap.getOrDefault(userId, new HashSet<>());

            if (stationId != null && !stopCalling) {
                String url = "https://bus.jeju.go.kr/api/searchArrivalInfoList.do?station_id=" + stationId;
                ResponseEntity<BusInfo[]> response = restTemplate.getForEntity(url, BusInfo[].class);
                BusInfo[] buses = response.getBody();
                log.info("Twilio Account SID: {}", accountSid);
                log.info("Twilio Auth Token: {}", authToken);

                if (buses != null) {
                    log.info("API 응답 데이터: {}", Arrays.toString(buses));
                    log.info("현재 {} 사용자의 cnt 값: {}", userId, cnt);

                    Arrays.stream(buses)
                            .filter(bus -> busId.equals(bus.getRouteNum()) && bus.getRemainStation() == station)
                            .forEach(bus -> {
                                if (!seenBuses.contains(bus.getVhId())) {
                                    log.info("{} 사용자에게 조건을 만족하는 새로운 버스를 찾았습니다: {}", userId, bus);
                                    seenBuses.add(bus.getVhId());
                                    cnt.incrementAndGet();  // 새로운 버스일 경우 카운터 증가
                                    userCntMap.put(userId, cnt);
                                    log.info("현재 {} 사용자의 cnt 값: {}", userId, cnt);
                                    Optional<Member> byPhone = memberRepository.findByPhone2(userId);
                                    if (byPhone.isPresent()) {
                                        Member member = byPhone.get();
                                        bus_call(member);
                                        // member에 대한 로직 처리
                                    } else {
                                        // 값이 없을 때의 처리 로직
                                        throw new NoSuchElementException("해당하는 사용자가 없습니다.");
                                    }

                                    if (cnt.get() >= 2) {
                                        log.info("{} 사용자의 cnt가 2에 도달하여 호출을 중단합니다.", userId);
                                        userStopCallingMap.put(userId, true);
                                    }
                                } else {
                                    log.info("{} 사용자는 이미 확인된 버스입니다: {}", userId, bus.getVhId());
                                }
                            });

                    if (cnt.get() < 2) {
                        log.info("{} 사용자에게 조건을 만족하는 새로운 버스를 찾지 못했습니다.", userId);
                    }
                } else {
                    log.warn("API 응답에서 버스 데이터가 비어 있습니다.");
                }

                // 상태 업데이트
                userSeenBusesMap.put(userId, seenBuses);
                userCntMap.put(userId, cnt);
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

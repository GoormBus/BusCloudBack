package goorm.domain.record.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Say;
import goorm.domain.member.domain.entity.Member;
import goorm.domain.member.domain.repository.MemberRepository;
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

    private final RestTemplate restTemplate = new RestTemplate();

    // 사용자별 상태를 저장하기 위한 Map
    private final Map<String, String> userBusIdMap = new ConcurrentHashMap<>();
    private final Map<String, String> userStationIdMap = new ConcurrentHashMap<>();
    private final Map<String, Integer> userStationMap = new ConcurrentHashMap<>();
    private final Map<String, Boolean> userStopCallingMap = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> userCntMap = new ConcurrentHashMap<>();
    private final Map<String, Set<Integer>> userSeenBusesMap = new ConcurrentHashMap<>();
    private static final long RUNNING_DURATION_HOURS = 3; // 3시간
    private final LocalDateTime startTime = LocalDateTime.now();

    private String accountSid="AC6daasdf7c0729c";


    private String authToken="4d";

    // 사용자별 API 호출 상태 설정 메서드
    @Async
    public void scheduleBusApiCall(String userId, String busId, String stationId, int station) {
        userBusIdMap.put(userId, busId);
        userStationIdMap.put(userId, stationId);
        userStationMap.put(userId, station);
        userStopCallingMap.put(userId, false);  // 호출 중단 플래그 초기화
        userSeenBusesMap.put(userId, new HashSet<>());  // 중복 체크 목록 초기화
        userCntMap.put(userId, new AtomicInteger(0));  // 카운터 초기화
    }

    // 5초마다 실행되는 메서드 - 사용자별로 독립적으로 동작
    @Scheduled(fixedRate = 5000)
    public void callBusApi() {
        // 현재 시간이 시작 시간으로부터 3시간 경과했는지 확인
        if (ChronoUnit.HOURS.between(startTime, LocalDateTime.now()) >= RUNNING_DURATION_HOURS) {
            log.info("3시간이 경과하여 스케줄링을 중단합니다.");
            return;  // 스케줄링 중단
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

// BusInfo 클래스는 API 응답에 맞게 필드 정의
class BusInfo {

    @JsonProperty("ARRV_STATION_ID")
    private int arrvStationId;

    @JsonProperty("ROUTE_SUB_NM")
    private String routeSubNm;

    @JsonProperty("VH_ID")
    private int vhId;

    @JsonProperty("ROUTE_ID")
    private int routeId;

    @JsonProperty("ROUTE_NUM")
    private String routeNum;

    @JsonProperty("PLATE_NO")
    private String plateNo;

    @JsonProperty("CURR_STATION_ID")
    private int currStationId;

    @JsonProperty("PREDICT_TRAV_TM")
    private int predictTravTm;

    @JsonProperty("REMAIN_STATION")
    private int remainStation;

    @JsonProperty("CURR_STATION_NM")
    private String currStationNm;

    @JsonProperty("LOW_PLATE_TP")
    private String lowPlateTp;

    // Getters and Setters
    public int getVhId() {
        return vhId;
    }

    public String getRouteNum() {
        return routeNum;
    }

    public int getRemainStation() {
        return remainStation;
    }

    @Override
    public String toString() {
        return "ROUTE_NUM=" + routeNum + ", REMAIN_STATION=" + remainStation;
    }
}

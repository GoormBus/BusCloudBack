package goorm.global.infra.feignclient;


import goorm.global.infra.feignclient.dto.ArrivalResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "jejuBusApi", url = "https://bus.jeju.go.kr/api")
public interface JejuBusClient {
    @GetMapping("/searchArrivalInfoList.do")
    ArrivalResponse getArrivalInfo(@RequestParam("station_id") String stationId);
}
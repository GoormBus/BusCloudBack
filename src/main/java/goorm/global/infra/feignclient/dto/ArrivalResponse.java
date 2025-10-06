package goorm.global.infra.feignclient.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ArrivalResponse {
    @JsonProperty("resultList")
    private List<ArrivalInfo> resultList;

    @Data
    public static class ArrivalInfo {
        @JsonProperty("route_name")
        private String routeName;

        @JsonProperty("remain_min")
        private String remainMin;

        @JsonProperty("remain_station")
        private String remainStation;

        @JsonProperty("plate_no")
        private String plateNo;

        // 필요하면 route_id, updown 등도 포함
    }
}

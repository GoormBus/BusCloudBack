package goorm.domain.buslog.presentation.dto.response;

import goorm.domain.buslog.domain.entity.BusLog;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;


@Builder
public record BusLogAllRes(

        @NotNull
        @Schema(description = "버스 기록 ID", example = "1")
        Long id,
        @NotBlank
        @Schema(description = "출발지", example = "제주공항")
        String departure,

        @NotBlank
        @Schema(description = "도착지", example = "제주시청")
        String destination,

        @NotBlank
        @Schema(description = "설정한 정류장 수", example = "3")
        Long station,

        @NotBlank
        @Schema(description = "노선 즉 버스ID ", example = "356")
        String notionId,

        @NotBlank
        @Schema(description = "정류장 ID", example = "54686")
        String stationId,

        @NotBlank
        @Schema(description = "알람 활성화", example = "true or false")
        boolean isAlarmFlag,

        @NotBlank
        @Schema(description = "즐겨찾기", example = "true or false")
        boolean isFavoriteFlag


) {
    public static BusLogAllRes of(BusLog busLog, boolean isAlarmFlag, boolean isFavoriteFlag) {
        return new BusLogAllRes(busLog.getId(), busLog.getDeparture(), busLog.getDestination(), busLog.getStation(),
                busLog.getNotionId(), busLog.getStationId(), isAlarmFlag, isFavoriteFlag);
    }
}

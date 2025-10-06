package goorm.domain.buslog.presentation.dto.response;

import goorm.domain.buslog.domain.entity.BusLog;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * 🚌 BusLogAllRes
 *
 * <p>사용자의 버스 기록 정보를 조회할 때 반환되는 응답 DTO입니다.</p>
 * <ul>
 *     <li>출발지, 도착지, 정류장 등 버스 이동 경로 정보 포함</li>
 *     <li>알림 활성 상태 및 즐겨찾기 여부 함께 제공</li>
 * </ul>
 */
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

        @NotNull
        @Schema(description = "설정한 정류장 수", example = "3")
        Long station,

        @NotBlank
        @Schema(description = "노선 ID (버스 번호)", example = "356")
        String notionId,

        @NotBlank
        @Schema(description = "정류장 ID", example = "54686")
        String stationId,

        @Schema(description = "알람 활성화 여부", example = "true")
        boolean isAlarmFlag,

        @Schema(description = "즐겨찾기 여부", example = "false")
        boolean isFavoriteFlag

) {
        /**
         * BusLog 엔티티와 부가 정보(알림/즐겨찾기 상태)를 DTO로 변환합니다.
         *
         * @param busLog BusLog 엔티티
         * @param isAlarmFlag 알림 활성 여부
         * @param isFavoriteFlag 즐겨찾기 여부
         * @return 변환된 BusLogAllRes DTO
         */
        public static BusLogAllRes of(BusLog busLog, boolean isAlarmFlag, boolean isFavoriteFlag) {
                return new BusLogAllRes(
                        busLog.getId(),
                        busLog.getDeparture(),
                        busLog.getDestination(),
                        busLog.getStation(),
                        busLog.getNotionId(),
                        busLog.getStationId(),
                        isAlarmFlag,
                        isFavoriteFlag
                );
        }
}

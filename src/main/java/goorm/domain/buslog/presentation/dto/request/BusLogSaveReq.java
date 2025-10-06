package goorm.domain.buslog.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 📝 BusLogSaveReq
 *
 * <p>버스 이용 기록을 새로 저장할 때 사용하는 요청 DTO입니다.</p>
 * <ul>
 *     <li>출발지와 도착지 정보</li>
 *     <li>정류장 ID 및 노선 ID 포함</li>
 * </ul>
 */
public record BusLogSaveReq(

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
        @Schema(description = "정류장 ID", example = "54686")
        String stationId,

        @NotBlank
        @Schema(description = "노선 ID (버스 번호)", example = "110")
        String notionId

) { }

package goorm.domain.buslog.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record NoteRequest(

        @NotBlank
        @Schema(description = "출발지", example = "제주공항")
        String departure,

        @NotBlank
        @Schema(description = "도착지", example = "제주시청")
        String destination,

        @NotBlank
        @Schema(description = "정류장 전", example = "3")
        Long station,

        @NotBlank
        @Schema(description = "정류장 ID", example = "54686")
        String stationId,

        @NotBlank
        @Schema(description = "노선 즉 버스ID ", example = "110")
        String notionId,

        @NotBlank
        @Schema(description = "넘겨준 시간", example = "23:00")
        String time


) {
}

package goorm.bus.record.dto.request;

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
        @Schema(description = "정류장", example = "3")
        String stopCnt,

        @NotBlank
        @Schema(description = "현재시간", example = "이건 아직 미정")
        String time

        ) {
}

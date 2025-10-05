package goorm.domain.buslog.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record StationRequest(

        @NotBlank
        @Schema(description = "정류장ID", example = "556456564")
        String stationId,

        @NotBlank
        @Schema(description = "버스ID", example = "12313")
        String busId,

        @NotBlank
        @Schema(description = "정류장 예약", example = "2")
        int station



        ) {
}

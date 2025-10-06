package goorm.domain.buslog.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record BusFavoriteReq(
        @NotBlank
        @Schema(description = "버스 기록 ID", example = "3")
        Long busLogId
        ) {
}

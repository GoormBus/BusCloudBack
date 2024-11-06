package goorm.bus.record.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NoteFavoriteRequest(
        @NotBlank
        @Schema(description = "해당 ID", example = "3")
        Long id,

        @NotNull
        @Schema(description = "줄격찾기", example = "true or false")
        boolean favorite ) {
}

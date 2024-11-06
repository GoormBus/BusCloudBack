package goorm.bus.record.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AlarmReq (
        @NotBlank
        @Schema(description = "해당 ID", example = "3")
        Long id,
        @NotNull
        @Schema(description = "true면 오면 true 바꾸기//", example = "true or false")
        boolean alarm
){

}

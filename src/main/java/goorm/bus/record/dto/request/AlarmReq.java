package goorm.bus.record.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AlarmReq (
        @NotBlank
        @Schema(description = "해당 ID", example = "3")
        Long id,
        @NotBlank
        @Schema(description = "true면 오면 true 바꾸기//", example = "true or false")
        boolean alarm
){

}

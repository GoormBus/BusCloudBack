package goorm.bus.ai.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;


public record AIRequest (
        @NotBlank
        @Schema(description = "음성 문구", example = "")
        String text
) {

}

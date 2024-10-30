package goorm.bus.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record MemberLoginReq (

        @NotBlank
        @Schema(description = "회원 id", example = "id")
        String loginId,

        @NotBlank
        @Schema(description = "회원 비밀번호", example = "pwd")
        String password
) {
}

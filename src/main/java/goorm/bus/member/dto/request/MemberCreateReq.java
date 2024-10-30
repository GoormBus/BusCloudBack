package goorm.bus.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record MemberCreateReq (

        @NotBlank
        @Schema(description = "회원 id", example = "id")
        String loginId,

        @NotBlank
        @Schema(description = "회원 비밀번호", example = "pwd")
        String password,

        @NotBlank
        @Schema(description = "회원 이름", example = "최승호")
        String name
) {
}

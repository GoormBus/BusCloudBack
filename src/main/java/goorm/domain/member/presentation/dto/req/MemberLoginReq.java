package goorm.domain.member.presentation.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record MemberLoginReq (

        @Schema(description = "회원 전화번호", example = "01033715386")
        String phone,

        @Schema(description = "회원 이름", example = "최승호")
        String name
) {
}

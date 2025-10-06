package goorm.domain.member.presentation.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 👤 MemberLoginReq
 *
 * <p>회원 로그인 및 자동 회원가입 요청 DTO입니다.</p>
 * <ul>
 *     <li>전화번호와 이름을 입력받아 인증을 진행합니다.</li>
 *     <li>전화번호는 중복 확인 및 신규 회원 여부 판별에 사용됩니다.</li>
 * </ul>
 */
public record MemberLoginReq(

        @NotBlank
        @Schema(description = "회원 전화번호", example = "01033715386")
        String phone,

        @NotBlank
        @Schema(description = "회원 이름", example = "최승호")
        String name

) { }

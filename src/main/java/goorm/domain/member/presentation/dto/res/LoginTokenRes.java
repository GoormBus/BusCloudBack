package goorm.domain.member.presentation.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 🔐 LoginTokenRes
 *
 * <p>회원 로그인 성공 시 발급되는 Access Token 및 Refresh Token 정보를 담는 응답 DTO입니다.</p>
 * <ul>
 *     <li>Access Token: 클라이언트 요청 인증용 JWT</li>
 *     <li>Refresh Token: 장기 세션 유지를 위한 JWT (쿠키로 전송됨)</li>
 * </ul>
 */
public record LoginTokenRes(

        @Schema(description = "Access Token", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0In0.abc123")
        String accessToken,

        @Schema(description = "Refresh Token 쿠키 문자열", example = "refreshToken=xyz456; Path=/; HttpOnly")
        String refreshToken

) {
    /**
     * Access/Refresh Token 정보를 포함한 LoginTokenRes 객체를 생성합니다.
     *
     * @param accessToken 발급된 Access Token
     * @param refreshToken 발급된 Refresh Token (쿠키 문자열 형태)
     * @return {@link LoginTokenRes} 인스턴스
     */
    public static LoginTokenRes of(String accessToken, String refreshToken) {
        return new LoginTokenRes(accessToken, refreshToken);
    }
}

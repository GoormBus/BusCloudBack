package goorm.domain.member.presentation.dto.res;

public record LoginTokenRes(
        String accessToken,
        String refreshToken
) {
    public static LoginTokenRes of(String accessToken, String refreshToken) {
        return new LoginTokenRes(accessToken, refreshToken);
    }
}

package goorm.domain.member.application.service.impl;

import goorm.domain.member.domain.entity.Role;
import goorm.domain.member.application.service.CreateAccessTokenAndRefreshTokenService;
import goorm.global.jwt.domain.entity.JsonWebToken;
import goorm.global.jwt.domain.repository.JsonWebTokenRepository;
import goorm.global.jwt.util.JWTUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Map;

/**
 * 🔑 CreateAccessTokenAndRefreshTokenServiceImpl
 *
 * <p>JWT를 생성하고 Refresh Token을 DB에 저장하는 서비스 구현체입니다.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CreateAccessTokenAndRefreshTokenServiceImpl implements CreateAccessTokenAndRefreshTokenService {

    private final JWTUtil jwtUtil;
    private final JsonWebTokenRepository jsonWebTokenRepository;

    /**
     * 🧾 Access/Refresh Token 생성 및 저장
     *
     * <p>JWTUtil을 이용하여 두 종류의 토큰을 생성하고,
     * Refresh Token은 DB에 저장하여 세션을 관리합니다.</p>
     *
     * @param userId 사용자 식별자
     * @param role 사용자 권한
     * @param phoneNumber 전화번호
     * @return Access Token과 Refresh Token 쿠키 문자열을 포함한 Map
     */
    @Override
    public Map<String, String> createAccessTokenAndRefreshToken(String userId, Role role, String phoneNumber) {
        String accessToken = jwtUtil.createAccessToken(userId, role, phoneNumber);
        String refreshToken = jwtUtil.createRefreshToken(userId, role, phoneNumber);

        JsonWebToken jsonWebToken = JsonWebToken.builder()
                .refreshToken(refreshToken)
                .providerId(userId)
                .role(role)
                .phoneNumber(phoneNumber)
                .build();

        jsonWebTokenRepository.save(jsonWebToken);

        String refreshTokenCookie = jwtUtil.createRefreshTokenCookie(refreshToken).toString();

        log.info("JWT 생성 완료: userId={}, role={}, phone={}", userId, role, phoneNumber);
        return Map.of("access_token", accessToken, "refresh_token_cookie", refreshTokenCookie);
    }
}

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

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CreateAccessTokenAndRefreshTokenServiceImpl implements CreateAccessTokenAndRefreshTokenService {

    private final JWTUtil jwtUtil;
    private final JsonWebTokenRepository jsonWebTokenRepository;

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

        return Map.of("access_token", accessToken, "refresh_token_cookie", refreshTokenCookie);
    }
}

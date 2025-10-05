package goorm.domain.member.application.service.impl;

import goorm.domain.member.application.service.ReissueService;
import goorm.domain.member.domain.entity.Role;
import goorm.global.infra.exception.error.GoormBusException;
import goorm.global.infra.exception.error.ErrorCode;
import goorm.global.jwt.domain.entity.JsonWebToken;
import goorm.global.jwt.domain.repository.JsonWebTokenRepository;
import goorm.global.jwt.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReissueServiceImpl implements ReissueService {

    private final JWTUtil jwtUtil;
    private final JsonWebTokenRepository jsonWebTokenRepository;

    @Override
    public void reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtUtil.getRefreshTokenFromCookies(request);

        if (!jwtUtil.jwtVerify(refreshToken, "refresh")) {
            log.info("Refresh token not valid");
            throw new GoormBusException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        JsonWebToken jsonWebToken = jsonWebTokenRepository.findById(refreshToken).orElse(null);

        if (jsonWebToken == null) {
            throw new GoormBusException(ErrorCode.REFRESH_TOKEN_NOT_EXIST);
        }

        String userId = jsonWebToken.getProviderId();
        Role role = jsonWebToken.getRole();
        String phoneNumber = jsonWebToken.getPhoneNumber();

        String newAccessToken = jwtUtil.createAccessToken(userId, role, phoneNumber);
        String newRefreshToken = jwtUtil.createRefreshToken(userId, role, phoneNumber);

        JsonWebToken newJsonWebToken = JsonWebToken.builder()
                .refreshToken(newRefreshToken)
                .phoneNumber(phoneNumber)
                .role(role)
                .build();

        jsonWebTokenRepository.delete(jsonWebToken);
        jsonWebTokenRepository.save(newJsonWebToken);

        response.addHeader("Authorization", "Bearer " + newAccessToken);
        response.addHeader(HttpHeaders.COOKIE, jwtUtil.createRefreshTokenCookie(newRefreshToken).toString());
    }
}

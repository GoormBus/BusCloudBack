package goorm.domain.member.application.service;

import goorm.domain.member.domain.entity.Role;
import java.util.Map;

/**
 * 🔑 CreateAccessTokenAndRefreshTokenService
 *
 * <p>JWT 기반 인증 시스템에서 Access Token과 Refresh Token을 생성하는 서비스입니다.</p>
 * <ul>
 *     <li>Access Token: 단기 인증용</li>
 *     <li>Refresh Token: 장기 세션 유지를 위한 토큰 (DB 저장 및 쿠키 전송)</li>
 * </ul>
 */
public interface CreateAccessTokenAndRefreshTokenService {

    /**
     * 🧾 Access Token 및 Refresh Token 생성
     *
     * <p>입력받은 사용자 정보(userId, role, phoneNumber)를 기반으로 JWT 토큰을 생성하고,
     * Refresh Token은 DB에 저장한 뒤 쿠키 문자열 형태로 반환합니다.</p>
     *
     * @param userId 사용자 식별자
     * @param role 사용자 권한 (Role)
     * @param phoneNumber 사용자 전화번호
     * @return Access Token과 Refresh Token 쿠키 문자열을 포함한 Map
     */
    Map<String, String> createAccessTokenAndRefreshToken(String userId, Role role, String phoneNumber);
}

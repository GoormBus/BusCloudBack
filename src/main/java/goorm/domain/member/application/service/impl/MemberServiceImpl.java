package goorm.domain.member.application.service.impl;

import goorm.domain.member.application.service.MemberService;
import goorm.domain.member.presentation.dto.req.MemberLoginReq;
import goorm.domain.member.domain.entity.Member;
import goorm.domain.member.domain.repository.MemberRepository;
import goorm.domain.member.application.service.CreateAccessTokenAndRefreshTokenService;
import goorm.domain.member.presentation.dto.res.LoginTokenRes;
import goorm.global.infra.exception.error.ErrorCode;
import goorm.global.infra.exception.error.GoormBusException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Map;

/**
 * 👤 MemberServiceImpl
 *
 * <p>회원 로그인 및 자동 회원가입 로직을 담당하는 구현체입니다.</p>
 * <ul>
 *     <li>전화번호로 회원 조회 → 없으면 신규 회원 등록</li>
 *     <li>JWT Access/Refresh Token 생성 및 반환</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final CreateAccessTokenAndRefreshTokenService createAccessTokenAndRefreshTokenService;
    private final MemberRepository memberRepository;

    /**
     * 🔐 로그인 및 회원가입 처리
     *
     * <p>전화번호를 기반으로 기존 회원을 조회하고,
     * 존재하지 않으면 새 회원을 등록합니다.</p>
     *
     * <p>이후 Access Token과 Refresh Token을 생성하여 클라이언트에 반환합니다.</p>
     *
     * @param req 로그인 요청 DTO (전화번호, 이름)
     * @return Access Token 및 Refresh Token 쿠키를 포함한 응답 객체
     * @throws GoormBusException 회원 정보를 찾을 수 없거나 유효하지 않을 경우
     */
    @Override
    public LoginTokenRes login(MemberLoginReq req) {
        Member findMember = memberRepository.findByPhone(req.phone()).orElse(null);

        // 기존 회원이 없을 경우 신규 생성
        if (findMember == null) {
            findMember = Member.builder()
                    .phone(req.phone())
                    .name(req.name())
                    .build();
            memberRepository.save(findMember);
            log.info("신규 회원 등록 완료: {}", req.phone());
        }

        // Access/Refresh Token 생성
        Map<String, String> tokens = createAccessTokenAndRefreshTokenService
                .createAccessTokenAndRefreshToken(findMember.getId(), findMember.getRole(), req.phone());

        String accessToken = tokens.get("access_token");
        String refreshTokenCookie = tokens.get("refresh_token_cookie");

        log.info("✅ Access Token: {}", accessToken);
        log.info("✅ Refresh Token Cookie: {}", refreshTokenCookie);

        return LoginTokenRes.of(accessToken, refreshTokenCookie);
    }
}

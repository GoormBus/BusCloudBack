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

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {
    private final CreateAccessTokenAndRefreshTokenService createAccessTokenAndRefreshTokenService;
    private final MemberRepository memberRepository;

    public LoginTokenRes login(MemberLoginReq req) {
        Member findMember = memberRepository.findByPhone(req.phone()).orElse(null);
        if (findMember == null) throw new GoormBusException(ErrorCode.USER_NOT_EXIST);

        Member newMember = Member.builder()
                .phone(req.phone())
                .name(req.name())
                .build();
        memberRepository.save(newMember);

        // 자체 access/refresh 토큰 생성
        Map<String, String> tokens = createAccessTokenAndRefreshTokenService.createAccessTokenAndRefreshToken(newMember.getId(), newMember.getRole(), req.phone());
        String accessToken = tokens.get("access_token");
        String refreshTokenCookie = tokens.get("refresh_token_cookie"); // ex) refreshToken=xxx; Path=/; HttpOnly


        log.info("✅ 엑세스:{}", accessToken);
        log.info("✅ 리프레쉬:{}", refreshTokenCookie);

        return LoginTokenRes.of(accessToken, refreshTokenCookie);
    }
}

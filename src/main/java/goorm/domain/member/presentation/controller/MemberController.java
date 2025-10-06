package goorm.domain.member.presentation.controller;

import goorm.domain.member.presentation.dto.req.MemberLoginReq;
import goorm.domain.member.application.service.impl.MemberServiceImpl;
import goorm.domain.member.presentation.dto.res.LoginTokenRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 👤 MemberController
 *
 * <p>회원 로그인 및 토큰 발급 API를 제공하는 컨트롤러입니다.</p>
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "회원(Member)", description = "회원 로그인 및 인증 API")
@RequestMapping("/api/member")
@Slf4j
public class MemberController {

    private final MemberServiceImpl memberService;

    /**
     * 🔐 로그인 및 자동 회원가입 API
     *
     * <p>전화번호 기반으로 회원을 조회하고,
     * 존재하지 않으면 자동으로 회원가입을 진행합니다.</p>
     *
     * <p>로그인 성공 시 Access Token과 Refresh Token 쿠키를 반환합니다.</p>
     *
     * @param req 로그인 요청 DTO
     * @return Access/Refresh Token 포함 응답
     */
    @Operation(summary = "회원가입 및 로그인", description = "전화번호 기반으로 인증 후 토큰을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증 성공"),
            @ApiResponse(responseCode = "404", description = "회원 정보 없음")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginTokenRes> login(@Valid @RequestBody MemberLoginReq req) {
        log.info("회원 로그인 요청 수신: {}", req.phone());
        return ResponseEntity.ok(memberService.login(req));
    }
}

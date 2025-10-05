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

@RestController
@RequiredArgsConstructor
@Tag(name = "회원(Member)")
@RequestMapping("/api/member")
@Slf4j
public class MemberController {

    private final MemberServiceImpl memberService;


    @Operation(summary = "회원가입&&로그인 자동", description = "사용자의 정보를 인증합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증 성공"),
            @ApiResponse(responseCode = "404", description = "인증 실패"),
    })
    @PostMapping("/login")
    public ResponseEntity<LoginTokenRes> login(@Valid @RequestBody MemberLoginReq req) {
        return ResponseEntity.ok(memberService.login(req));
    }
}

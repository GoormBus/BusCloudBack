package goorm.bus.member.controller;

import goorm.bus.global.dto.response.JwtTokenSet;
import goorm.bus.global.dto.response.SuccessResponse;
import goorm.bus.global.dto.response.result.SingleResult;
import goorm.bus.member.dto.request.MemberCreateReq;
import goorm.bus.member.dto.request.MemberLoginReq;
import goorm.bus.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "회원(Member)")
@RequestMapping("/api/member")
@Slf4j
public class MemberController {

    private final MemberService memberService;



    @PostMapping("/login")
    @Operation(summary = "로그인")
    public SuccessResponse<SingleResult<JwtTokenSet>> login(@Valid @RequestBody MemberLoginReq req) {
        SingleResult<JwtTokenSet> result = memberService.login(req);
        return SuccessResponse.ok(result);
    }
}

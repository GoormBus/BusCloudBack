package goorm.bus.member.service;

import goorm.bus.global.dto.response.JwtTokenSet;
import goorm.bus.global.dto.response.result.SingleResult;
import goorm.bus.global.exception.CustomException;
import goorm.bus.global.exception.ErrorCode;
import goorm.bus.global.service.AuthService;
import goorm.bus.global.service.ResponseService;
import goorm.bus.member.dto.request.MemberCreateReq;
import goorm.bus.member.dto.request.MemberLoginReq;
import goorm.bus.member.entity.Member;
import goorm.bus.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final AuthService authService;

//    public SingleResult<JwtTokenSet> register(MemberCreateReq req) {
//        // 폰 중복 체크
//        if (memberRepository.existByPhone(req.phone())) {
//            throw new CustomException(ErrorCode.USER_ALREADY_EXIST);
//        }
//        Member newMember = Member.builder()
//                .memberId(UUID.randomUUID().toString())
//                .phone(req.phone())
//                .name(req.name())
//                .build();
//        newMember = memberRepository.save(newMember);
//
//        JwtTokenSet jwtTokenSet = authService.generateToken(newMember.getMemberId());
//
//        return ResponseService.getSingleResult(jwtTokenSet);
//    }

    public SingleResult<JwtTokenSet> login(MemberLoginReq req) {
        Optional<Member> findMember = memberRepository.findByPhone(req.phone());
        if(findMember.isEmpty()){
            Member newMember = Member.builder()
                    .memberId(UUID.randomUUID().toString())
                    .phone(req.phone())
                    .build();
            newMember = memberRepository.save(newMember);
            JwtTokenSet jwtTokenSet = authService.generateToken(newMember.getMemberId());

            return ResponseService.getSingleResult(jwtTokenSet);
        }

        Member member =findMember.get();

        JwtTokenSet jwtTokenSet = authService.generateToken(member.getMemberId());

        return ResponseService.getSingleResult(jwtTokenSet);
    }
}

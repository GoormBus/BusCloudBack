package goorm.domain.member.application.service;

import goorm.domain.member.presentation.dto.req.MemberLoginReq;
import goorm.domain.member.presentation.dto.res.LoginTokenRes;

/**
 * 👤 MemberService
 *
 * <p>사용자 회원가입 및 로그인 관련 핵심 비즈니스 로직을 정의하는 인터페이스입니다.</p>
 * <ul>
 *     <li>전화번호 기반 회원 조회 및 신규 등록</li>
 *     <li>Access Token 및 Refresh Token 발급</li>
 * </ul>
 */
public interface MemberService {

    /**
     * 🔐 로그인 및 회원가입 처리
     *
     * <p>전화번호를 기반으로 회원 정보를 조회하고,
     * 존재하지 않으면 신규 회원으로 등록합니다.</p>
     *
     * <p>그 후 Access Token과 Refresh Token을 생성하여 반환합니다.</p>
     *
     * @param req 로그인 요청 DTO (전화번호, 이름)
     * @return Access Token 및 Refresh Token 쿠키 정보
     * @throws goorm.global.infra.exception.error.GoormBusException 회원 정보가 유효하지 않을 경우
     */
    LoginTokenRes login(MemberLoginReq req);
}

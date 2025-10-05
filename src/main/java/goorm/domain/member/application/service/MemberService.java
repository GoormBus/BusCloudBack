package goorm.domain.member.application.service;

import goorm.domain.member.presentation.dto.req.MemberLoginReq;
import goorm.domain.member.presentation.dto.res.LoginTokenRes;

public interface MemberService {
    LoginTokenRes login(MemberLoginReq req);
}

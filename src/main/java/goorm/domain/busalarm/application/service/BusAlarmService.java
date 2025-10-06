package goorm.domain.busalarm.application.service;

import goorm.domain.busalarm.presentation.dto.req.BusAlarmReq;
import goorm.domain.buslog.domain.entity.BusLog;
import goorm.domain.member.domain.entity.Member;

public interface BusAlarmService {
    void updateBusAlarm(BusAlarmReq req);
    void sendBusArrivalVoiceNotification(Member member, BusLog busLog);
}

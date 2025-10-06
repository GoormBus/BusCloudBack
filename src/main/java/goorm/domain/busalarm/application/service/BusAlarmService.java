package goorm.domain.busalarm.application.service;

import goorm.domain.busalarm.presentation.dto.req.BusAlarmReq;

public interface BusAlarmService {
    void updateBusAlarm(BusAlarmReq req);
}

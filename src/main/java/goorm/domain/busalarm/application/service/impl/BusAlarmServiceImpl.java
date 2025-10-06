package goorm.domain.busalarm.application.service.impl;

import goorm.domain.busalarm.application.service.BusAlarmService;
import goorm.domain.busalarm.domain.entity.BusAlarm;
import goorm.domain.busalarm.domain.repository.BusAlarmRepository;
import goorm.domain.buslog.domain.entity.BusLog;
import goorm.domain.buslog.domain.repository.BusLogRepository;
import goorm.domain.busalarm.presentation.dto.req.BusAlarmReq;
import goorm.global.infra.exception.error.ErrorCode;
import goorm.global.infra.exception.error.GoormBusException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class BusAlarmServiceImpl implements BusAlarmService {

    private final BusAlarmRepository busAlarmRepository;
    private final BusLogRepository busLogRepository;

    @Override
    public void updateBusAlarm(BusAlarmReq req) {
        BusLog findBusLog = busLogRepository.findById(req.busLogId()).orElse(null);
        if (findBusLog == null) throw new GoormBusException(ErrorCode.BUS_LOG_NOT_EXIST);

        BusAlarm findBusAlarm = busAlarmRepository.findByBusLog(findBusLog).orElse(null);
        if (findBusAlarm == null) throw new GoormBusException(ErrorCode.BUS_ALARM_NOT_EXIST);

        if (findBusAlarm.isAlarmFlag()) findBusAlarm.deactivateIsAlarmFlag();
        else findBusAlarm.activateIsAlarmFlag();
    }
}

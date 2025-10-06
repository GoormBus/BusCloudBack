package goorm.domain.buslog.application.service.impl;


import goorm.domain.busalarm.domain.entity.BusAlarm;
import goorm.domain.busalarm.domain.repository.BusAlarmRepository;
import goorm.domain.buslog.application.service.BusLogService;
import goorm.domain.buslog.application.service.StationService;
import goorm.domain.buslog.domain.entity.BusFavorite;
import goorm.domain.buslog.domain.entity.BusLog;
import goorm.domain.buslog.domain.repository.BusFavoriteRepository;
import goorm.domain.buslog.presentation.dto.request.BusFavoriteReq;
import goorm.domain.member.domain.entity.Member;
import goorm.domain.member.domain.repository.MemberRepository;
import goorm.domain.buslog.presentation.dto.request.BusLogSaveReq;
import goorm.domain.buslog.presentation.dto.response.BusLogAllRes;
import goorm.domain.buslog.domain.repository.BusLogRepository;
import goorm.global.infra.exception.error.ErrorCode;
import goorm.global.infra.exception.error.GoormBusException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BusLogServiceImpl implements BusLogService {

    private final BusLogRepository busLogRepository;
    private final BusFavoriteRepository busFavoriteRepository;
    private final BusAlarmRepository busAlarmRepository;

    private final MemberRepository memberRepository;

    private final StationService stationService;

    @Override
    public void postBusLogSave(BusLogSaveReq req, String userId){

        Member findMember = memberRepository.findById(userId).orElse(null);
        if(findMember == null) throw new GoormBusException(ErrorCode.USER_NOT_EXIST);

        BusLog newBusLog= BusLog.builder()
                .member(findMember)
                .departure(req.departure())
                .destination(req.destination())
                .station(req.station())
                .notionId(req.notionId())
                .stationId(req.stationId())
                .build();

        busLogRepository.save(newBusLog);
    }

    @Override
    public List<BusLogAllRes> getBusLogAll(String memberId) {
        Member findMember = memberRepository.findById(memberId).orElse(null);
        if(findMember == null) throw new GoormBusException(ErrorCode.USER_NOT_EXIST);

        List<BusLogAllRes> result = new ArrayList<>();
        List<BusLog> busLogs = busLogRepository.findByMember(findMember);

        for (BusLog busLog : busLogs) {
            BusAlarm findBusAlarm = busAlarmRepository.findByBusLog(busLog).orElse(null);
            if(findBusAlarm==null) throw new GoormBusException(ErrorCode.BUS_ALARM_NOT_EXIST);

            BusFavorite findBusFavorite = busFavoriteRepository.findByBusLog(busLog).orElse(null);
            if(findBusFavorite==null) throw new GoormBusException(ErrorCode.BUS_FAVORITE_NOT_EXIST);

            result.add(BusLogAllRes.of(busLog, findBusAlarm.isAlarmFlag(), findBusFavorite.isFavoriteFlag()));
        }

        return result;
    }


    @Override
    public void updateBusFavorite(BusFavoriteReq req){
        BusLog findBusLog = busLogRepository.findById(req.busLogId()).orElse(null);
        if(findBusLog == null) throw new GoormBusException(ErrorCode.BUS_LOG_NOT_EXIST);

        BusFavorite findBusFavorite = busFavoriteRepository.findByBusLog(findBusLog).orElse(null);
        if(findBusFavorite==null) throw new GoormBusException(ErrorCode.BUS_FAVORITE_NOT_EXIST);

        if(findBusFavorite.isFavoriteFlag()) findBusFavorite.deactivateIsFavoriteFlag();
        else findBusFavorite.activateIsFavoriteFlag();

    }



}

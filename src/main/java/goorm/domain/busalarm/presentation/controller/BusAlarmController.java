package goorm.domain.busalarm.presentation.controller;

import goorm.domain.busalarm.application.service.BusAlarmService;
import goorm.domain.busalarm.domain.entity.BusAlarm;
import goorm.domain.busalarm.presentation.dto.req.BusAlarmReq;
import goorm.domain.buslog.application.service.BusLogServiceImpl;
import goorm.domain.buslog.application.service.StationService;
import goorm.domain.buslog.domain.repository.BusLogRepository;
import goorm.domain.buslog.presentation.dto.request.BusFavoriteReq;
import goorm.domain.buslog.presentation.dto.request.BusLogSaveReq;
import goorm.domain.buslog.presentation.dto.response.BusLogAllRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "사용자의 버스 알림 정보")
@RequestMapping("/api/bus/alarm")
@Slf4j
public class BusAlarmController {

    private final BusAlarmService busAlarmService;


    @Operation(summary = "버스 알림 활성화 및 비활성화 API")
    @PostMapping
    public ResponseEntity<Void> updateBusAlarm(@Valid @RequestBody BusAlarmReq req) {
        busAlarmService.updateBusAlarm(req);
        return ResponseEntity.ok().build();
    }


}

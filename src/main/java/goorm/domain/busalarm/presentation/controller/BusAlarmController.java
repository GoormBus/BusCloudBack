package goorm.domain.busalarm.presentation.controller;

import goorm.domain.busalarm.application.service.BusAlarmService;
import goorm.domain.busalarm.presentation.dto.req.BusAlarmReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

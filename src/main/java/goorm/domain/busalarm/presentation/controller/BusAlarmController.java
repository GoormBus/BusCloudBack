package goorm.domain.busalarm.presentation.controller;

import goorm.domain.busalarm.application.service.BusAlarmService;
import goorm.domain.busalarm.presentation.dto.req.BusAlarmReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 🔔 BusAlarmController
 *
 * <p>사용자의 버스 도착 알림 활성/비활성화를 담당하는 REST 컨트롤러입니다.</p>
 * <ul>
 *     <li>버스 알림 활성화 / 비활성화 요청 처리</li>
 *     <li>사용자의 버스 로그에 연결된 알림 상태 변경</li>
 * </ul>
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "사용자의 버스 알림 정보", description = "버스 알림 설정 관련 API")
@RequestMapping("/api/bus/alarm")
@Slf4j
public class BusAlarmController {

    private final BusAlarmService busAlarmService;

    /**
     * 🚦 버스 알림 활성화 및 비활성화 API
     *
     * <p>사용자가 특정 버스 로그에 대해 알림을 켜거나 끌 수 있습니다.</p>
     *
     * @param req 알림 상태 변경 요청 DTO
     * @return HTTP 200 OK (성공 시)
     */
    @Operation(summary = "버스 알림 활성화 및 비활성화", description = "버스 로그별 알림 상태를 토글합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "버스 알림 상태 변경 성공"),
            @ApiResponse(responseCode = "400", description = "요청 데이터가 유효하지 않음"),
            @ApiResponse(responseCode = "404", description = "버스 로그 또는 알림 정보 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping
    public ResponseEntity<Void> updateBusAlarm(@Valid @RequestBody BusAlarmReq req) {
        busAlarmService.updateBusAlarm(req);
        log.info("버스 알림 상태 변경 요청 처리 완료: busLogId={}", req.busLogId());
        return ResponseEntity.ok().build();
    }
}

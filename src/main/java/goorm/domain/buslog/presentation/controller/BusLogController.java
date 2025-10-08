package goorm.domain.buslog.presentation.controller;

import goorm.domain.buslog.application.service.BusLogService;
import goorm.domain.buslog.presentation.dto.request.BusFavoriteReq;
import goorm.domain.buslog.presentation.dto.request.BusLogSaveReq;
import goorm.domain.buslog.presentation.dto.response.BusLogAllRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "사용자의 버스 기록 정보", description = "버스 로그, 즐겨찾기 관련 API")
@RequestMapping("/api/bus")
@Slf4j
public class BusLogController {

    private final BusLogService busLogService;

    /**
     * 버스 로그 저장 API
     * - 사용자 요청 시 새로운 버스 로그를 저장함
     * - 즐겨찾기는 기본적으로 false로 설정
     */
    @Operation(summary = "버스 로그 저장", description = "버스 데이터 정보를 저장합니다. (즐겨찾기 기본값 false)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "버스 로그 저장 성공"),
            @ApiResponse(responseCode = "400", description = "요청 데이터가 유효하지 않음"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping
    public ResponseEntity<Void> saveBusLog(@Valid @RequestBody BusLogSaveReq req,
                                           @AuthenticationPrincipal String userId) {
        busLogService.postBusLogSave(req, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 버스 로그 전체 조회 API
     * - 사용자의 모든 버스 로그를 조회
     * - 각 로그마다 알림 여부, 즐겨찾기 여부 포함
     */
    @Operation(summary = "버스 로그 전체 조회", description = "사용자의 모든 버스 기록을 조회합니다. (알림 여부, 즐겨찾기 여부 포함)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "버스 로그 조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자 또는 버스 로그가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping
    public ResponseEntity<List<BusLogAllRes>> getBusLogAll(@RequestParam String memberId,@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(busLogService.getBusLogAll(memberId));
    }

    /**
     * 즐겨찾기 상태 토글 API
     * - true ↔ false 상태 전환
     */
    @Operation(summary = "버스 즐겨찾기 상태 변경", description = "해당 버스 로그의 즐겨찾기 상태를 토글합니다. (활성/비활성)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "즐겨찾기 상태 변경 성공"),
            @ApiResponse(responseCode = "400", description = "요청 데이터가 유효하지 않음"),
            @ApiResponse(responseCode = "404", description = "버스 로그 또는 즐겨찾기 정보 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/favorite")
    public ResponseEntity<Void> updateBusFavorite(@Valid @RequestBody BusFavoriteReq req) {
        busLogService.updateBusFavorite(req);
        return ResponseEntity.ok().build();
    }
}

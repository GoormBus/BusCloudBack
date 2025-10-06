package goorm.domain.buslog.presentation.controller;

import goorm.domain.buslog.application.service.BusLogService;
import goorm.domain.buslog.presentation.dto.request.BusFavoriteReq;
import goorm.domain.buslog.presentation.dto.request.NoteFavoriteRequest;
import goorm.domain.buslog.presentation.dto.request.BusLogSaveReq;
import goorm.domain.buslog.presentation.dto.response.BusLogAllRes;
import goorm.domain.buslog.domain.repository.BusLogRepository;
import goorm.domain.buslog.application.service.impl.BusLogServiceImpl;
import goorm.domain.buslog.application.service.StationService;
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
@Tag(name = "사용자의 버스 기록 정보")
@RequestMapping("/api/bus")
@Slf4j
public class BusLogController {

    private final BusLogService busLogService;

    private final StationService stationService;

    private final BusLogRepository noteRepository;


    @Operation(summary = "버스 데이터 정보들이 쭉 넘어옴 여기서 전화 콜 및 일시 저장해줘야됨 즉 즐격찾기 false")
    @PostMapping
    public ResponseEntity<Void> saveBusLog(@Valid @RequestBody BusLogSaveReq req,
                                       @AuthenticationPrincipal String userId) {

        busLogService.postBusLogSave(req, userId);


        String stationId = req.stationId();
        int station = req.station();
        String busId = req.notionId();
        // 5초마다 API 호출 시작
        stationService.scheduleBusApiCall(userId, busId, stationId, station);

        // BusLog 객체를 NoteSaveResponse로 변환
        NoteSaveResponse response = NoteSaveResponse.of(note.getData());

        // 변환된 NoteSaveResponse를 응답으로 반환
        return SuccessResponse.ok(response);
        // 즉시 note 객체 응답 반환
    }

    @Operation(summary = "즐겨찾기 버스 기록 저장들 보여주기")
    @GetMapping
    public ResponseEntity<List<BusLogAllRes>> getBusLogAll(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(busLogService.getBusLogAll(userId));
    }


    @Operation(summary = "즐겨 찾기 API")
    @PostMapping("/favorite")
    public ResponseEntity<Void> updateBusFavorite(@Valid @RequestBody BusFavoriteReq req) {
        busLogService.updateBusFavorite(req);
        return ResponseEntity.ok().build();
    }



}

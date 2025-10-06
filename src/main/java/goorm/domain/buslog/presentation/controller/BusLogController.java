package goorm.domain.buslog.presentation.controller;

import goorm.domain.buslog.domain.entity.BusLog;
import goorm.domain.buslog.presentation.dto.request.AlarmReq;
import goorm.domain.buslog.presentation.dto.request.NoteFavoriteRequest;
import goorm.domain.buslog.presentation.dto.request.BusLogSaveReq;
import goorm.domain.buslog.presentation.dto.response.BusLogAllRes;
import goorm.domain.buslog.domain.repository.BusLogRepository;
import goorm.domain.buslog.application.service.BusLogServiceImpl;
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
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Tag(name = "사용자의 버스 기록 정보")
@RequestMapping("/api/bus")
@Slf4j
public class BusLogController {

    private final BusLogServiceImpl busLogServiceImpl;

    private final StationService stationService;

    private final BusLogRepository noteRepository;


    @Operation(summary = "버스 데이터 정보들이 쭉 넘어옴 여기서 전화 콜 및 일시 저장해줘야됨 즉 즐격찾기 false")
    @PostMapping("/save")
    public ResponseEntity<Void> saveBusLog(@Valid @RequestBody BusLogSaveReq req,
                                       @AuthenticationPrincipal String userId) {

        busLogServiceImpl.save(req, userId);


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


    @GetMapping("/list")
    @Operation(summary = "즐겨찾기 버스 기록 저장 보여주기")
    public ResponseEntity<List<BusLogAllRes>> getBusLogAll(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(busLogServiceImpl.findAll(userId));
    }



    @PostMapping("/favorite")
    @Operation(summary = "즐겨 찾기 API")
    public ResponseEntity<String> delete(@Valid @RequestBody NoteFavoriteRequest req) {
        Optional<BusLog> findNote = noteRepository.findById(req.id());
        if (findNote.isPresent()) {
            BusLog busLog = findNote.get();
            if (req.favorite()) {
                noteRepository.updelete(req, 2);
            } else {
                if (busLog.getFavorite_pre() != 1) {
                    noteRepository.delete(req);
                }
            }

        } else {
            // 값이 없을 때의 처리 로직
            throw new NoSuchElementException("해당하는 사용자가 없습니다.");
        }
        // 즉시 응답 반환
        return ResponseEntity.ok("설정 완료");
    }

    @PostMapping("/alarm")
    @Operation(summary = "알람 on off")
    public ResponseEntity<String> alarm(@Valid @RequestBody AlarmReq req) {
        log.info("123123");
        noteRepository.updateAlarm(req);

        // 즉시 응답 반환
        return ResponseEntity.ok("업데이트 완료");
    }

//    @PostMapping("/station")
//    @Operation(summary = "버스 전화 API //2")
//    public ResponseEntity<String> station(@Valid @RequestAttribute("id") String userId, @RequestBody StationRequest req){
//
//        String busId = req.busId();
//        String stationId = req.stationId();
//        int station = req.station();
//
//        // 30초마다 API 호출 시작
//        stationService.scheduleBusApiCall(userId,busId, stationId,station);
//
//        // 즉시 응답 반환
//        return ResponseEntity.ok("API 호출이 시작되었습니다.");
//    }

}

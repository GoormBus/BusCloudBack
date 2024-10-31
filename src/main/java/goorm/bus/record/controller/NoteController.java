package goorm.bus.record.controller;

import goorm.bus.global.dto.response.SuccessResponse;
import goorm.bus.global.dto.response.result.ListResult;
import goorm.bus.global.dto.response.result.SingleResult;
import goorm.bus.global.service.ResponseService;
import goorm.bus.record.dto.request.NoteRequest;
import goorm.bus.record.dto.request.StationRequest;
import goorm.bus.record.dto.response.NoteResponse;
import goorm.bus.record.entity.Note;
import goorm.bus.record.repository.NoteRepository;
import goorm.bus.record.service.NoteService;
import goorm.bus.record.service.StationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "사용자의 버스 기록 정보")
@RequestMapping("/api/bus")
@Slf4j
public class NoteController {

    private final NoteService noteService;

    private final StationService stationService;

    private final NoteRepository noteRepository;

    @GetMapping("/list")
    @Operation(summary = "최근 버스 기록 저장 보여주기")
    public SuccessResponse<ListResult<NoteResponse>> readAll(  @RequestAttribute("id") String userId) {
        ListResult<NoteResponse> result = noteService.findAll(userId);
        return SuccessResponse.ok(result);
    }



    @PostMapping
    @Operation(summary = "버스 출발, 도착, 정류장 수 받기")
    public SuccessResponse<SingleResult<Note>> create(@Valid @RequestBody NoteRequest req
    ) {
        SingleResult<Note> save = noteService.save(req);
        return SuccessResponse.ok(save);
    }


    @PostMapping("/station")
    public ResponseEntity<String> station(@Valid @RequestAttribute("id") String userId, @RequestBody StationRequest req){        String busId = req.busId();
        String stationId = req.stationId();
        int station = req.station();

        // 30초마다 API 호출 시작
        stationService.scheduleBusApiCall(userId,busId, stationId,station);

        // 즉시 응답 반환
        return ResponseEntity.ok("API 호출이 시작되었습니다.");
    }

}

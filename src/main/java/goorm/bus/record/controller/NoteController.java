package goorm.bus.record.controller;

import goorm.bus.global.dto.response.SuccessResponse;
import goorm.bus.global.dto.response.result.ListResult;
import goorm.bus.global.dto.response.result.SingleResult;
import goorm.bus.record.dto.request.AlarmReq;
import goorm.bus.record.dto.request.NoteFavoriteRequest;
import goorm.bus.record.dto.request.NoteRequest;
import goorm.bus.record.dto.response.NoteResponse;
import goorm.bus.record.dto.response.NoteSaveResponse;
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

import java.util.NoSuchElementException;
import java.util.Optional;

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
    @Operation(summary = "즐겨찾기 버스 기록 저장 보여주기")
    public SuccessResponse<ListResult<NoteResponse>> readAll(@RequestAttribute("id") String userId) {
        ListResult<NoteResponse> note = noteService.findAll(userId);
        return SuccessResponse.ok(note);
    }



    @PostMapping("/save")
    @Operation(summary = "버스 데이터 정보들이 쭉 넘어옴 여기서 전화 콜 및 일시 저장해줘야됨 즉 즐격찾기 false")
    public SuccessResponse<NoteSaveResponse> create(@Valid
                                                          @RequestAttribute("id") String userId,
                                                    @RequestBody NoteRequest req
    ) {
        SingleResult<Note> note = noteService.save(req,userId);



        String stationId = req.stationId();
        int station = req.station();
        String busId =req.notionId();
        // 5초마다 API 호출 시작
        stationService.scheduleBusApiCall(userId,busId, stationId,station);

        // Note 객체를 NoteSaveResponse로 변환
        NoteSaveResponse response = NoteSaveResponse.of(note.getData());

        // 변환된 NoteSaveResponse를 응답으로 반환
        return SuccessResponse.ok(response);
        // 즉시 note 객체 응답 반환
    }


    @PostMapping("/favorite")
    @Operation(summary = "즐겨 찾기 API")
    public ResponseEntity<String> delete(@Valid @RequestBody NoteFavoriteRequest req){
        Optional<Note> findNote =noteRepository.findById(req.id());
        if (findNote.isPresent()) {
            Note note = findNote.get();
            if(req.favorite()){
                noteRepository.updelete(req,2);
            }else{
                if(note.getFavorite_pre()!=1){
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
    public ResponseEntity<String> alarm(@Valid @RequestBody AlarmReq req){
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

package goorm.bus.record.service;

import goorm.bus.global.dto.response.result.ListResult;
import goorm.bus.global.dto.response.result.SingleResult;
import goorm.bus.global.service.ResponseService;
import goorm.bus.record.dto.request.NoteRequest;
import goorm.bus.record.dto.response.NoteResponse;
import goorm.bus.record.entity.Note;
import goorm.bus.record.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoteService {

    private final NoteRepository noteRepository;

    public SingleResult<Note> save(NoteRequest req){
        Note newRecord= Note.builder()
                .departure(req.departure())
                .destination(req.destination())
                .stopCnt(req.stopCnt())
                .time(req.time())
                .alarm(true)
                .build();

        Note save = noteRepository.save(newRecord);
        return ResponseService.getSingleResult(save);
    }
    public ListResult<NoteResponse> findAll() {
        List<Note> notes = noteRepository.findAll();
        List<NoteResponse> list = notes.stream().map(NoteResponse::of).toList();
        return ResponseService.getListResult(list);
    }

    

}

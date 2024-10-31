package goorm.bus.record.dto.response;

import goorm.bus.record.entity.Note;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;


@Builder
public record NoteResponse(

        @NotNull
        @Schema(description = "게시물 ID", example = "1")
        Long id,
        @NotBlank
        @Schema(description = "출발지", example = "제주공항")
        String departure,

        @NotBlank
        @Schema(description = "도착지", example = "제주시청")
        String destination,

        @NotBlank
        @Schema(description = "정류장", example = "3")
        String stopCnt,

        @NotBlank
        @Schema(description = "현재시간", example = "이건 아직 미정")
        String time,


        @NotBlank
        @Schema(description = "알람활성화", example = "true or false")
        boolean alarm

) {
    public static NoteResponse of(Note note) {
        return NoteResponse.builder()
                .id(note.getId())
                .departure(note.getDeparture())
                .destination(note.getDestination())
                .stopCnt(note.getStopCnt())
                .time(note.getTime())
                .alarm(note.getAlarm())
                .build();
    }
}

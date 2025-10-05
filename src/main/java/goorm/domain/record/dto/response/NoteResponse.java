package goorm.domain.record.dto.response;

import goorm.domain.record.entity.Note;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;


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
        int station,

        @NotBlank
        @Schema(description = "넘겨준 시간", example = "23:00")
        String time,


        @NotBlank
        @Schema(description = "알람활성화", example = "true or false")
        boolean alarm,

        @NotBlank
        @Schema(description = "즐겨찾기", example = "true or false")
        boolean favorite,

        @NotBlank
        @Schema(description = "노선 즉 버스ID ", example = "356")
        String notionId,

        @NotBlank
        @Schema(description = "정류장 ID", example = "54686")
        String stationId,

        @NotBlank
        @Schema(description = "최대빈도수", example = "2")
        int frequency

) {
    public static NoteResponse of(Note note) {
        return NoteResponse.builder()
                .id(note.getId())
                .departure(note.getDeparture())
                .destination(note.getDestination())
                .station(note.getStation())
                .time(note.getTime())
                .alarm(note.isAlarm())
                .frequency(note.getFrequency())
                .build();
    }
}

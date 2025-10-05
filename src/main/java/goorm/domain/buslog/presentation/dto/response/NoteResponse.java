package goorm.domain.buslog.presentation.dto.response;

import goorm.domain.buslog.domain.entity.BusLog;
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
    public static NoteResponse of(BusLog busLog) {
        return NoteResponse.builder()
                .id(busLog.getId())
                .departure(busLog.getDeparture())
                .destination(busLog.getDestination())
                .station(busLog.getStation())
                .time(busLog.getTime())
                .alarm(busLog.isAlarm())
                .frequency(busLog.getFrequency())
                .build();
    }
}

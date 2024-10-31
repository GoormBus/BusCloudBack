package goorm.bus.ai.dto.response;

import goorm.bus.ai.entity.Search;
import goorm.bus.record.dto.response.NoteResponse;
import goorm.bus.record.entity.Note;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AIResponse (
        @NotBlank
        @Schema(description = "출발지", example = "제주스테이션")
        String departure,


        @NotBlank
        @Schema(description = "도착지", example = "제주시청")
        String destination,

        @NotBlank
        @Schema(description = "정류장", example = "3")
        String station

) {
    public static AIResponse of(Search search) {
        return AIResponse.builder()
                .departure(search.getDeparture())
                .destination(search.getDestination())
                .station(search.getStation())
                .build();
    }
}

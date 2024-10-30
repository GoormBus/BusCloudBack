package goorm.bus.global.dto.response.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class ExceptionResult {

    @Getter
    @Setter
    @Builder
    public static class ServerErrorData {

        @Schema(description = "오류 발생 클래스", example = "org.example.XX")
        private String errorClass;

        @Schema(description = "오류 메세지")
        private String errorMessage;
    }

    @Getter
    @Setter
    @Builder
    public static class ParameterData {

        @Schema(description = "오류가 발생한 필드", example = "title")
        private String key;

        @Schema(description = "넣은 요청값", example = "null")
        private String value;

        @Schema(description = "오류 발생 이유", example = "공백일 수 없습니다")
        private String reason;
    }
}

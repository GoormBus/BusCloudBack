package goorm.bus.global.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ErrorResponse<T> {

    @Schema(description = "성공 여부", example = "false")
    private boolean success = false;

    @Schema(description = "예외 코드", example = "-100")
    private int code;

    @Schema(description = "예외 메세지", example = "실패하였습니다.")
    private String message;

    @Schema(description = "예외 참고 데이터")
    private T result;

    public ErrorResponse(int code, String message, T result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }

    public static <T> ErrorResponse<T> of(int code, String message) {
        return new ErrorResponse<>(code, message, null);
    }

    public static <T> ErrorResponse<T> of(int code, String message, T data) {
        return new ErrorResponse<>(code, message, data);
    }
}

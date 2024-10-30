package goorm.bus.global.dto.response.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseState {

    SUCCESS(1, "성공하였습니다."),
    FAIL(-1, "실패하였습니다.");

    private int code;
    private String message;
}

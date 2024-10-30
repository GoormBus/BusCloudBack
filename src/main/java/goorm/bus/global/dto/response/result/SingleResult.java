package goorm.bus.global.dto.response.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SingleResult<T> {

    @Schema(description = "단일 데이터")
    private T data;
}

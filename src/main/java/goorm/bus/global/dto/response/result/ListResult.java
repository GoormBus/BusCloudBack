package goorm.bus.global.dto.response.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ListResult<T> {

    @Schema(description = "리스트 데이터")
    private List<T> list;
}

package goorm.domain.buslog.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * ⭐ BusFavoriteReq
 *
 * <p>사용자가 버스 로그의 즐겨찾기 상태를 변경할 때 사용하는 요청 DTO입니다.</p>
 */
public record BusFavoriteReq(

        @NotBlank
        @Schema(description = "버스 로그 ID", example = "3")
        Long busLogId

) { }

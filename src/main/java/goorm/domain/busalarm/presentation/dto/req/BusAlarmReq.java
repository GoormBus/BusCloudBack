package goorm.domain.busalarm.presentation.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 🔔 BusAlarmReq
 *
 * <p>버스 알림 활성화 또는 비활성화를 요청할 때 사용하는 DTO입니다.</p>
 * <ul>
 *     <li>특정 버스 로그 ID를 기반으로 알림 상태를 토글합니다.</li>
 *     <li>사용자는 이 요청을 통해 개별 노선의 알림을 켜거나 끌 수 있습니다.</li>
 * </ul>
 */
public record BusAlarmReq(

        @NotBlank
        @Schema(description = "버스 로그 ID", example = "3")
        Long busLogId

) { }

package goorm.bus.global.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtTokenSet {

    @Schema(description = "타입", example = "Bearer")
    private String grantType;

    @Schema(description = "토큰", example = "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwiaWF0IjoxNzI2NjUyNDM0LCJleHAiOjE3MjY2NTMwMzR9.g3U4UWTsqxEWipOzZT6v-9tpUkVjuPNhD2AkB5nAiFM")
    private String token;
}

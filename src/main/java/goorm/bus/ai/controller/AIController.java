package goorm.bus.ai.controller;

import goorm.bus.ai.dto.request.AIRequest;
import goorm.bus.ai.dto.response.AIResponse;
import goorm.bus.ai.service.AIService;
import goorm.bus.global.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Open AI 호출")
@RequestMapping("/api/ai")
@Slf4j
public class AIController {

    private final AIService aiService;
    @PostMapping
    @Operation(summary = "음성으로 해서 온 문자를 open ai로 키워드 도출해서 주기")
    public SuccessResponse<AIResponse> create(@Valid @RequestBody AIRequest req
    ) {
        AIResponse aiResponse = aiService.aiText(req.text());
        return SuccessResponse.ok(aiResponse);
    }

}

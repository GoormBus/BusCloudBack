package goorm.bus.ai.service;

import goorm.bus.ai.dto.request.AIRequest;
import goorm.bus.ai.dto.response.AIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIService {
    @Value("${openai.key}")
    private String openaiApiKey;
    public AIResponse aiText(String text){

        String userInput =text+ "---------" +
                "" +
                "이게 사용자로부터 온 문장인데 여기서 출발지, 도착지, 정류장을 추출해서 그것만 뽑아줘" +
                "3개의 단어가 나올거 잖아 각각 ,을 구분해서 줘 예로 들어서 서울,인천,3" +
                "아 추가로 오개전이면 5로 해석 즉 십개전이면 10 이런식으로 해야돼" ;

        // OpenAI API 호출
        String answer = "";
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.openai.com/v1/chat/completions"))
                    .header("Authorization", "Bearer " + openaiApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(new JSONObject()
                            .put("model", "gpt-4o")
                            .put("messages", new JSONArray()
                                    .put(new JSONObject().put("role", "system").put("content", "너는 뉴스 주요문장을 생성해주는 사람이야"))
                                    .put(new JSONObject().put("role", "user").put("content", userInput)))
                            .put("max_tokens", 1000)
                            .toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject jsonResponse = new JSONObject(response.body());
            answer = jsonResponse.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");


        } catch (Exception e) {
            e.printStackTrace();
        }


        String[] split = answer.split(",");
        AIResponse aiResponse=AIResponse.builder()
                .destination(split[1])
                .departure(split[0])
                .station(split[2])
                .build();
        return aiResponse;

    }
}

package goorm.bus.global.config;

import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class AIConfig {
    @Value("${openai.key}")
    private String apiKey;


    @Bean
    public OpenAiService getOpenAiService() {
        return new OpenAiService(apiKey, Duration.ofSeconds(30));
    }
}

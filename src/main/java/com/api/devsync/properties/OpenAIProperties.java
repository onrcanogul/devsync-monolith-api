package com.api.devsync.properties;

import com.theokanning.openai.service.OpenAiService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

@Getter
@Setter
@ConfigurationProperties(prefix = "openai")
public class OpenAIProperties {

    @Value("${openai.token}")
    private String token;

    @Bean
    public OpenAiService openAiService() {
        return new OpenAiService(token);
    }
}
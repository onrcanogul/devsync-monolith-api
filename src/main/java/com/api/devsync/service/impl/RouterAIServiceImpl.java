package com.api.devsync.service.impl;

import com.api.devsync.properties.OpenRouterProperties;
import com.api.devsync.service.AIService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class RouterAIServiceImpl implements AIService{

    private final OpenRouterProperties properties;
    private final WebClient client;
    public RouterAIServiceImpl(WebClient.Builder webClientBuilder, OpenRouterProperties properties) {
        this.properties = properties;
        this.client = webClientBuilder
                .baseUrl(properties.getBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getToken())
                .build();
    }

    public String send(String llm, String prompt) {
        Map<String, Object> body = Map.of(
                "model", properties.getModel(),
                "messages", new Object[]{
                        Map.of("role", "user", "content", prompt)
                }
        );
        return client.post()
                .uri("/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getToken())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}

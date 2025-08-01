package com.api.devsync.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "openrouter")
public class OpenRouterProperties {
    private String token;
    private String baseUrl;
    private String model;
}
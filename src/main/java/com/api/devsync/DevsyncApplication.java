package com.api.devsync;

import com.api.devsync.properties.GitHubProperties;
import com.api.devsync.properties.OpenAIProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({GitHubProperties.class, OpenAIProperties.class})
public class DevsyncApplication {
	public static void main(String[] args) {
		SpringApplication.run(DevsyncApplication.class, args);
	}
}

package com.api.devsync.service.impl;

import com.api.devsync.properties.OpenAIProperties;
import com.api.devsync.service.AIService;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class OpenAiServiceImpl implements AIService {

    private final OpenAiService service ;

    public OpenAiServiceImpl(OpenAIProperties openAIConfigurations) {
        this.service = new OpenAiService(openAIConfigurations.getToken(), Duration.ofSeconds(300));
    }

    @Override
    public String send(String llm, String prompt) {
        ChatMessage userMessage = new ChatMessage("user", prompt);

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(llm)
                .messages(List.of(userMessage))
                .temperature(0.7)
                .maxTokens(3000)
                .build();
        List<ChatCompletionChoice> choices = service.createChatCompletion(request).getChoices();
        if (!choices.isEmpty()) return choices.get(0).getMessage().getContent();
        return "[No response from OpenAI]";
    }
}

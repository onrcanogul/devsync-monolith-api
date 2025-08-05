package com.api.devsync.service.impl;

import com.api.devsync.constant.Prompts;
import com.api.devsync.model.dto.*;
import com.api.devsync.service.AIService;
import com.api.devsync.service.PullRequestAnalyzerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class PullRequestAnalyzerServiceImpl implements PullRequestAnalyzerService {

    private final ObjectMapper objectMapper;
    private final AIService aiService;

    public PullRequestAnalyzerServiceImpl(ObjectMapper objectMapper, AIService aiService) {
        this.objectMapper = objectMapper;
        this.aiService = aiService;
    }

    public AnalyzeAIDto analyze(PrepareAnalyzeDto model) throws JsonProcessingException {
        String prompt = Prompts.prAnalyzePrompt(objectMapper.writeValueAsString(model));
        String answer = aiService.send("gpt-4o-mini", prompt);
        return objectMapper.readValue(cleanJson(answer), AnalyzeAIDto.class);
    }


    private String cleanJson(String rawJson) {
        return rawJson
                .replace("```json", "")
                .replace("```", "")
                .trim();
    }
}
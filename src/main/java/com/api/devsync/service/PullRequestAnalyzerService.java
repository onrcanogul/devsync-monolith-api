package com.api.devsync.service;


import com.api.devsync.model.dto.AnalyzeAIDto;
import com.api.devsync.model.fromWebhook.GithubWebhookModel;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface PullRequestAnalyzerService {
    AnalyzeAIDto analyze(GithubWebhookModel model) throws JsonProcessingException;
}

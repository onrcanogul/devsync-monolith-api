package com.api.devsync.service;

import com.api.devsync.model.dto.AnalyzeDto;
import com.api.devsync.model.fromWebhook.GithubWebhookModel;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import java.util.UUID;

public interface AnalyzeService {
    List<AnalyzeDto> get(int page, int size);
    List<AnalyzeDto> getByRepository(Long repoId);
    AnalyzeDto getById(UUID id);
    AnalyzeDto getByPullRequest(Long pullRequestId);
    AnalyzeDto createAnalyze(GithubWebhookModel model) throws JsonProcessingException;
}

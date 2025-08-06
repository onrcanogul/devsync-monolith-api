package com.api.devsync.service;


import com.api.devsync.entity.PullRequestAnalysis;
import com.api.devsync.model.dto.AnalyzeAIDto;
import com.api.devsync.model.dto.PrepareAnalyzeDto;
import com.fasterxml.jackson.core.JsonProcessingException;


public interface PullRequestAnalyzerService {
    void applyAiAnalysisToPullRequest(PullRequestAnalysis analyze, AnalyzeAIDto aiResult);
    AnalyzeAIDto analyze(PrepareAnalyzeDto model) throws JsonProcessingException;
}

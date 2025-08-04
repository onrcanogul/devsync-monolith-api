package com.api.devsync.service;


import com.api.devsync.model.dto.AnalyzeAIDto;
import com.api.devsync.model.dto.PrepareAnalyzeDto;
import com.fasterxml.jackson.core.JsonProcessingException;


public interface PullRequestAnalyzerService {
    AnalyzeAIDto analyze(PrepareAnalyzeDto model) throws JsonProcessingException;
}

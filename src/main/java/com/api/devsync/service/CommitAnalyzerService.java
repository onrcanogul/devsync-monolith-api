package com.api.devsync.service;

import com.api.devsync.entity.PullRequestAnalysis;
import com.api.devsync.model.dto.AnalyzeAIDto;

public interface CommitAnalyzerService {
    void applyAiAnalysisToCommits(PullRequestAnalysis analyze, AnalyzeAIDto aiResult);
}

package com.api.devsync.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AnalyzeAIDto {
    private PullRequestAIAnalyzeDto pullRequestAnalysis;
    private List<CommitAIAnalyzeDto> commitAnalyses;
}

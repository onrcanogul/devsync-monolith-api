package com.api.devsync.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommitAnalysisDto extends AnalyzeDto {
    private String id;
    private String message;
    private PullRequestAnalysisDto pullRequestAnalyze;
}
package com.api.devsync.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class CommitAnalysisDto extends AnalyzeDto {
    private UUID id;
    private String hash;
    private String message;
    private PullRequestAnalysisDto pullRequestAnalyze;
}
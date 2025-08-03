package com.api.devsync.model.dto;

import com.api.devsync.entity.Commit;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommitAnalysisDto extends AnalyzeDto {
    private String id;
    private String message;
    private Commit commit;
    private PullRequestAnalysisDto pullRequestAnalyze;
}
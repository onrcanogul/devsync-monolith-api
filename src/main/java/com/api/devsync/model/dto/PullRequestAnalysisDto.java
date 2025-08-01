package com.api.devsync.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class PullRequestAnalysisDto extends AnalyzeDto {
    private UUID id;
    private long pullRequestId;
    private List<CommitAnalysisDto> commitAnalysis = new ArrayList<>();
}

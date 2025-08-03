package com.api.devsync.model.dto;

import com.api.devsync.entity.PullRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class PullRequestAnalysisDto extends AnalyzeDto {
    private UUID id;
    private PullRequest pullRequest;
    private List<CommitAnalysisDto> commitAnalysis = new ArrayList<>();
}

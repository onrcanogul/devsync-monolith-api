package com.api.devsync.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PullRequestAIAnalyzeDto {
    private int riskScore;
    private String riskReason;
    private String technicalComment;
    private String functionalComment;
    private String architecturalComment;
}


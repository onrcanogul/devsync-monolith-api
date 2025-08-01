package com.api.devsync.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnalyzeDto {
    private String repoName;
    private Long repoId;
    private String branch;
    private String author;
    private int commitCount;
    private int fileChangeCount;
    private int totalAdditions;
    private int totalDeletions;
    private int riskScore;
    private String technicalComment;
    private String functionalComment;
    private String architecturalComment;
    private LocalDateTime analyzedAt;
}

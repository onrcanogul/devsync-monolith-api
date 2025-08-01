package com.api.devsync.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter @Setter
@MappedSuperclass
@ToString
public class Analyze {
    private String repoName;
    private Long repoId;
    private String branch;
    private String author;
    private int commitCount;
    private int fileChangeCount;
    private int totalAdditions;
    private int totalDeletions;
    private int riskScore;
    @Column(columnDefinition = "text")
    private String technicalComment;
    @Column(columnDefinition = "text")
    private String functionalComment;
    @Column(columnDefinition = "text")
    private String architecturalComment;
    private LocalDateTime analyzedAt;
}

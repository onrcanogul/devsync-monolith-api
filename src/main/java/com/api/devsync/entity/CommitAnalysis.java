package com.api.devsync.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "commit-analysis")
public class CommitAnalysis extends Analyze {
    @Id
    private String id;
    private String message;
    @ManyToOne
    @JoinColumn(name = "pull_request_analyze_id")
    private PullRequestAnalysis pullRequestAnalyze;
}

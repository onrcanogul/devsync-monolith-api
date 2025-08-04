package com.api.devsync.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "commit_analysis")
public class CommitAnalysis extends Analyze {

    @Id
    @Column(name = "id")
    private String id;
    private String author;
    private String message;

    @OneToOne
    @JoinColumn(name = "commit_hash", referencedColumnName = "hash")
    @JsonIgnore
    private Commit commit;

    @ManyToOne
    @JoinColumn(name = "pull_request_analyze_id")
    private PullRequestAnalysis pullRequestAnalyze;
}
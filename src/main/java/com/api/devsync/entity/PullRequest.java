package com.api.devsync.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PullRequest {
    @Id
    private Long id;
    private String branch;
    private String pusher;
    private String headCommitMessage;
    private String headCommitSha;
    private int commitCount;

    @OneToMany(mappedBy = "pullRequest")
    private List<Commit> commits;

    @OneToOne
    @JoinColumn(name = "analysis_id", referencedColumnName = "id")
    private PullRequestAnalysis analysis;

    @ManyToOne
    @JoinColumn(name = "createdBy_id")
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "repository_id")
    private Repository repository;
}
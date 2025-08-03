package com.api.devsync.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @OneToMany(mappedBy = "pullRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Commit> commits;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "analysis_id", referencedColumnName = "id")
    private PullRequestAnalysis analysis;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "repository_id")
    private Repository repository;

    private LocalDateTime analyzedDate;
}
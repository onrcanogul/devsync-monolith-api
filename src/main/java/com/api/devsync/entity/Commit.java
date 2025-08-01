package com.api.devsync.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Commit {
    @Id
    private String hash;
    private String message;

    @ManyToOne
    @JoinColumn(name = "pull_request_id")
    private PullRequest pullRequest;

    @OneToOne
    @MapsId
    @JoinColumn(name = "hash")
    private CommitAnalysis analysis;

    public Commit(String hash, String message) {
        this.hash = hash;
        this.message = message;
    }
}

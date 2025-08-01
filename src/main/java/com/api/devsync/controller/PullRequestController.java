package com.api.devsync.controller;

import com.api.devsync.entity.PullRequest;
import com.api.devsync.service.PullRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pull-request")
public class PullRequestController {
    private final PullRequestService pullRequestService;

    public PullRequestController(PullRequestService pullRequestService) {
        this.pullRequestService = pullRequestService;
    }
    @GetMapping("{id}")
    public ResponseEntity<PullRequest> getById(@PathVariable Long id) {
        return ResponseEntity.ok(pullRequestService.getById(id));
    }

    @GetMapping("user/{username}")
    public ResponseEntity<List<PullRequest>> getByUser(@PathVariable String username) {
        return ResponseEntity.ok(pullRequestService.getByUser(username));
    }

    @GetMapping("repo/{repoId}")
    public ResponseEntity<List<PullRequest>> get(@PathVariable Long repoId) {
        return ResponseEntity.ok(pullRequestService.get(repoId));
    }

    @GetMapping("repo/{repoId}/{branch}")
    public ResponseEntity<List<PullRequest>> get(@PathVariable String branch, @PathVariable Long repoId) {
        return ResponseEntity.ok(pullRequestService.get(repoId, branch));
    }
}

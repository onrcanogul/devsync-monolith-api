package com.api.devsync.controller;

import com.api.devsync.entity.Commit;
import com.api.devsync.service.CommitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/commit")
public class CommitController {
    private final CommitService commitService;

    public CommitController(CommitService commitService) {
        this.commitService = commitService;
    }

    @GetMapping
    public ResponseEntity<List<Commit>> getCommitNode() {
        return ResponseEntity.ok(commitService.get());
    }
}

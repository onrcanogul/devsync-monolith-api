package com.api.devsync.controller;

import com.api.devsync.entity.Repository;
import com.api.devsync.service.RepositoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/repository")
public class RepositoryController {
    private final RepositoryService repositoryService;

    public RepositoryController(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @GetMapping("{username}")
    public ResponseEntity<List<Repository>> getRepositoriesByUser(@PathVariable String username) {
        return ResponseEntity.ok(repositoryService.getByUser(username));
    }
}

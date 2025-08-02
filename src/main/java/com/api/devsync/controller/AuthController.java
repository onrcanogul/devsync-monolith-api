package com.api.devsync.controller;

import com.api.devsync.service.GithubAuthService;
import com.api.devsync.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final GithubAuthService gitHubOAuthService;
    private final JwtService jwtService;

    public AuthController(GithubAuthService gitHubOAuthService, JwtService jwtService) {
        this.gitHubOAuthService = gitHubOAuthService;
        this.jwtService = jwtService;
    }

    @PostMapping("/github/code")
    public ResponseEntity<?> handleGitHubLogin(@RequestBody Map<String, String> payload) {

        return ResponseEntity.ok(Map.of("token", "jwt"));
    }

    @PostMapping("/github/token/{username}")
    public String getGitHubAccessToken(@PathVariable String username) {
        return gitHubOAuthService.getGithubAccessToken(username);
    }


}


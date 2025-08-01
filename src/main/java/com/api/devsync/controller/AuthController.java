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
        //todo -> service
        String code = payload.get("code");

        String accessToken = gitHubOAuthService.getAccessToken(code);
        Map<String, Object> userInfo = gitHubOAuthService.getUserInfo(accessToken);

        String githubId = userInfo.get("id").toString();
        String username = userInfo.get("login").toString();
        String email = userInfo.get("email") != null ? userInfo.get("email").toString() : username + "@github.com";

        gitHubOAuthService.saveGithubToken(username, accessToken, code);

        String jwt = jwtService.generateToken(username, githubId, email);

        return ResponseEntity.ok(Map.of("token", jwt));
    }

    @PostMapping("/github/token/{username}")
    public String getGitHubAccessToken(@PathVariable String username) {
        return gitHubOAuthService.getGithubAccessToken(username);
    }


}


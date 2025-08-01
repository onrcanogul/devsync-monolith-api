package com.api.devsync.service.impl;

import com.api.devsync.client.GitApiClient;
import com.api.devsync.constant.AppConstants;
import com.api.devsync.model.fromApi.RepositoryFromApi;
import com.api.devsync.model.fromWebhook.GithubWebhookModel;
import com.api.devsync.repository.GithubTokenRepository;
import com.api.devsync.service.GitService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GitServiceImpl implements GitService {
    private final GitApiClient gitApiClient;
    private final GithubTokenRepository githubTokenRepository;

    public GitServiceImpl(GitApiClient gitApiClient, GithubTokenRepository githubTokenRepository) {
        this.gitApiClient = gitApiClient;
        this.githubTokenRepository = githubTokenRepository;
    }

    @Override
    public void handlePullRequest(GithubWebhookModel model) {

    }

    @Override
    public List<RepositoryFromApi> getRepositories(String username) {
        String githubAccessToken = githubTokenRepository.findByUsername(username).get().getToken();
        return gitApiClient.getUsersRepositories(username, githubAccessToken, AppConstants.webhookUrl);
    }

    @Override
    public void addWebhook(String accessToken, String owner, String repo) {
        HttpHeaders headers = new HttpHeaders();
        String githubAccessToken = githubTokenRepository.findByUsername(owner).get().getToken();
        headers.setBearerAuth(githubAccessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> config = new HashMap<>();
        config.put("url", AppConstants.webhookUrl);
        config.put("content_type", "json");
        config.put("insecure_ssl", "0");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", "web");
        requestBody.put("active", true);
        requestBody.put("events", List.of("push"));
        requestBody.put("config", config);
        gitApiClient.addWebhook(owner, repo, requestBody, headers);
    }
}

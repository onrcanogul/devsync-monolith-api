package com.api.devsync.service;

import com.api.devsync.model.fromApi.repository.RepositoryFromApi;
import com.api.devsync.model.fromWebhook.GithubWebhookModel;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface GitService {
    void handlePullRequest(GithubWebhookModel model) throws JsonProcessingException;
    List<RepositoryFromApi> getRepositories(String username);
    void addWebhook(String accessToken, String owner, String repo);
}

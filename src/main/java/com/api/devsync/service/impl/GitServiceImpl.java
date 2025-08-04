package com.api.devsync.service.impl;

import com.api.devsync.client.GitApiClient;
import com.api.devsync.constant.AppConstants;
import com.api.devsync.exception.NotFoundException;
import com.api.devsync.model.dto.PrepareAnalyzeDto;
import com.api.devsync.model.dto.PullRequestAnalysisDto;
import com.api.devsync.model.dto.PullRequestWithAnalysisDto;
import com.api.devsync.model.fromApi.commit.CommitResponseFromApi;
import com.api.devsync.model.fromApi.repository.RepositoryFromApi;
import com.api.devsync.model.fromWebhook.GithubWebhookModel;
import com.api.devsync.repository.GithubTokenRepository;
import com.api.devsync.service.AnalyzeService;
import com.api.devsync.service.GitService;
import com.api.devsync.service.PullRequestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GitServiceImpl implements GitService {
    private final GitApiClient gitApiClient;
    private final GithubTokenRepository githubTokenRepository;
    private final AnalyzeService analyzeService;
    private final PullRequestService pullRequestService;

    public GitServiceImpl(GitApiClient gitApiClient, GithubTokenRepository githubTokenRepository, AnalyzeService analyzeService, PullRequestService pullRequestService) {
        this.gitApiClient = gitApiClient;
        this.githubTokenRepository = githubTokenRepository;
        this.analyzeService = analyzeService;
        this.pullRequestService = pullRequestService;
    }

    @Override
    public void handlePullRequest(GithubWebhookModel model) throws JsonProcessingException {
        PrepareAnalyzeDto prepareAnalyzeDto = setCommitDetails(model);
        PullRequestAnalysisDto analyze = analyzeService.createAnalyze(prepareAnalyzeDto);
        PullRequestWithAnalysisDto pullRequestWithAnalysisDto = new PullRequestWithAnalysisDto(model, analyze);
        pullRequestService.save(pullRequestWithAnalysisDto);
    }

    @Override
    public List<RepositoryFromApi> getRepositories(String username) {
        String githubAccessToken = githubTokenRepository.findByUsername(username).orElseThrow(
                () -> new NotFoundException("githubTokenNotFound")).getToken();
        return gitApiClient.getUsersRepositories(username, githubAccessToken, AppConstants.webhookUrl);
    }

    @Override
    public void addWebhook(String accessToken, String owner, String repo) {
        HttpHeaders headers = new HttpHeaders();
        String githubAccessToken = githubTokenRepository.findByUsername(owner).orElseThrow(
                () -> new NotFoundException("githubTokenNotFound")
        ).getToken();
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


    private PrepareAnalyzeDto setCommitDetails(GithubWebhookModel model) {
        String githubAccessToken = githubTokenRepository.findByUsername(model.getSender().getLogin())
                .orElseThrow(() -> new NotFoundException("githubTokenNotFound")).getToken();

        List<CommitResponseFromApi> commits = new ArrayList<>();
        model.getCommits().forEach(c -> commits.add(gitApiClient.getCommit(c.getAuthor().getName(), model.getRepository().getName(), c.getId(), "123123")));

        return PrepareAnalyzeDto.builder().fullName(model.getRepository().getFull_name())
                .repositoryName(model.getRepository().getName())
                .repositoryDescription(model.getRepository().getDescription())
                .branchName(model.getRef())
                .repoId(model.getRepository().getId())
                .commits(commits)
                .build();
    }
}

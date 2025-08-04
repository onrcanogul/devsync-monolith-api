package com.api.devsync.client;

import com.api.devsync.model.fromApi.commit.CommitResponseFromApi;
import com.api.devsync.model.fromApi.repository.RepositoryFromApi;
import com.api.devsync.model.fromWebhook.Repository;
import com.api.devsync.properties.GitHubProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
public class GitApiClient {

    private final WebClient webClient;

    public GitApiClient(WebClient.Builder builder, GitHubProperties properties) {
        this.webClient = builder
                .baseUrl("https://api.github.com")
                .defaultHeader("Authorization", "Bearer " + properties.getToken())
                .defaultHeader("Accept", "application/vnd.github+json")
                .build();
    }

    public List<RepositoryFromApi> getUsersRepositories(String username, String accessToken, String targetWebhookUrl) {
        return webClient.get()
                .uri("https://api.github.com/users/{username}/repos", username)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToFlux(RepositoryFromApi.class)
                .flatMap(repo ->
                        webClient.get()
                                .uri("/repos/{owner}/{repo}/hooks", repo.getOwner().getLogin(), repo.getName())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                                .retrieve()
                                .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {})
                                .collectList()
                                .map(hooks -> {
                                    boolean hasTargetWebhook = hooks.stream().anyMatch(hook -> {
                                        Map<String, Object> config = (Map<String, Object>) hook.get("config");
                                        return config != null && targetWebhookUrl.equals(config.get("url"));
                                    });
                                    repo.setHasTargetWebhook(hasTargetWebhook);
                                    return repo;
                                })
                )
                .collectList()
                .block();
    }

    public CommitResponseFromApi getCommit(String owner, String repo, String sha, String token) {
        return webClient.get()
                .uri("/repos/{owner}/{repo}/commits/{sha}", owner, repo, sha)
//                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/vnd.github.v3+json")
                .retrieve()
                .bodyToMono(CommitResponseFromApi.class)
                .block();
    }

    public Repository getRepositoryDetails(String accessToken, String owner, String repo) {
        String url = String.format("https://api.github.com/repos/%s/%s", owner, repo);

        WebClient webClient = WebClient.builder().build();

        return webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(Repository.class)
                .block();
    }

    public void addWebhook(String owner, String repo, Map<String, Object> requestBody, HttpHeaders headers) {
        String url = String.format("https://api.github.com/repos/%s/%s/hooks", owner, repo);

        WebClient webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.AUTHORIZATION, headers.getFirst(HttpHeaders.AUTHORIZATION))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        webClient.post()
                .uri(url)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> System.err.println(error.getMessage()))
                .block();
    }



}

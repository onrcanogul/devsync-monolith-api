package com.api.devsync.service.impl;

import com.api.devsync.entity.GithubToken;
import com.api.devsync.exception.NotFoundException;
import com.api.devsync.repository.GithubTokenRepository;
import com.api.devsync.service.GithubAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class GithubAuthServiceImpl implements GithubAuthService {

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    private GithubTokenRepository githubTokenRepository;

    public GithubAuthServiceImpl(GithubTokenRepository githubTokenRepository) {
        log.info("client-id: {}", clientId);
        log.info("client-secret: {}", clientSecret);
        this.githubTokenRepository = githubTokenRepository;
    }

    public String getAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://github.com/login/oauth/access_token";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.valueOf("application/vnd.github.v3+json")));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("code", code);
        body.add("redirect_uri", "https://devsyncweb.site/oauth/callback");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        log.info("request: {}", request.toString());
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
        log.info("response: {}", response.getBody());
        return (String) response.getBody().get("access_token");
    }

    public Map<String, Object> getUserInfo(String accessToken) {
        String url = "https://api.github.com/user";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);

        return response.getBody();
    }

    public String getGithubAccessToken(String username) {
        GithubToken token = githubTokenRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("tokenNotFound"));
        return token.getToken();
    }

    public void saveGithubToken(String username, String accessToken, String code) {
        Optional<GithubToken> githubToken = githubTokenRepository.findByUsername(username);
        if (githubToken.isPresent()) {
            GithubToken token = githubToken.get();
            token.setToken(accessToken);
            githubTokenRepository.save(token);
        }
        else {
            GithubToken newGithubToken = new GithubToken();
            newGithubToken.setCode(code);
            newGithubToken.setUsername(username);
            newGithubToken.setToken(accessToken);
            newGithubToken.setCreatedDate(LocalDateTime.now());
            githubTokenRepository.save(newGithubToken);
        }
    }
}

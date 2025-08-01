package com.api.devsync.service;

import java.util.Map;

public interface GithubAuthService {
    String getAccessToken(String code);
    Map<String, Object> getUserInfo(String accessToken);
    String getGithubAccessToken(String username);
    void saveGithubToken(String username, String accessToken, String code);
}

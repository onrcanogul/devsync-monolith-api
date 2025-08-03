package com.api.devsync.service;

import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generateToken(UserDetails userDetails);
    String generateToken(String username, String githubId, String email);
    boolean isTokenValid(String token, String username);
    String extractUsername(String token);
    List<String> extractRoles(String token);
}
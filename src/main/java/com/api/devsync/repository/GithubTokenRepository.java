package com.api.devsync.repository;

import com.api.devsync.entity.GithubToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GithubTokenRepository extends JpaRepository<GithubToken, UUID> {
    Optional<GithubToken> findByUsername(String username);
}

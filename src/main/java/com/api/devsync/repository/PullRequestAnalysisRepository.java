package com.api.devsync.repository;

import com.api.devsync.entity.PullRequestAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PullRequestAnalysisRepository extends JpaRepository<PullRequestAnalysis, UUID> {
    List<PullRequestAnalysis> findByPullRequest_Repository_Id(Long repoId);
    Optional<PullRequestAnalysis> findByPullRequestId(Long pullRequestId);
}

package com.api.devsync.repository;

import com.api.devsync.entity.PullRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PullRequestRepository extends JpaRepository<PullRequest, Long> {
    List<PullRequest> findAllByRepositoryId(Long repositoryId);
    List<PullRequest> findByBranchAndRepository_Id(String branch, Long repository_id);
    List<PullRequest> findByRepository_OwnerLogin(String ownerLogin);
}

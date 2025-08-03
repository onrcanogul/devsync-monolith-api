package com.api.devsync.repository;

import com.api.devsync.entity.CommitAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommitAnalysisRepository extends JpaRepository<CommitAnalysis, UUID> {
    Optional<CommitAnalysis> findByHash(String hash);
}

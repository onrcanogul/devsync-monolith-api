package com.api.devsync.repository;

import com.api.devsync.entity.CommitAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommitAnalysisRepository extends JpaRepository<CommitAnalysis, String> {
}

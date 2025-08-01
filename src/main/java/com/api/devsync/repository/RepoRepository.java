package com.api.devsync.repository;

import com.api.devsync.entity.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface RepoRepository extends JpaRepository<Repository, Long> {
    List<Repository> findByOwnerLogin(String ownerLogin);
}


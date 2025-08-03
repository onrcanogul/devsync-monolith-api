package com.api.devsync.repository;

import com.api.devsync.entity.Commit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommitRepository extends JpaRepository<Commit, String> {
    Optional<Commit> findByHash(String hash);
}

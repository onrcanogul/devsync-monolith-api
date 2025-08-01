package com.api.devsync.service.impl;

import com.api.devsync.entity.Repository;
import com.api.devsync.repository.RepoRepository;
import com.api.devsync.service.RepositoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepositoryServiceImpl implements RepositoryService {
    private final RepoRepository repoRepository;

    public RepositoryServiceImpl(RepoRepository repoRepository) {
        this.repoRepository = repoRepository;
    }

    @Override
    public List<Repository> getByUser(String username) {
        return repoRepository.findByOwnerLogin(username);
    }
}

package com.api.devsync.service.impl;

import com.api.devsync.entity.*;
import com.api.devsync.exception.NotFoundException;
import com.api.devsync.mapper.PullRequestMapper;
import com.api.devsync.model.dto.PullRequestWithAnalysisDto;
import com.api.devsync.repository.*;
import com.api.devsync.service.PullRequestService;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PullRequestServiceImpl implements PullRequestService {
    private final PullRequestRepository pullRequestRepository;
    private final CommitRepository commitRepository;
    private final CommitAnalysisRepository commitAnalysisRepository;
    private final RepoRepository repositoryRepository;
    private final UserRepository userRepository;
    private final EntityManager entityManager;

    public PullRequestServiceImpl(PullRequestRepository pullRequestRepository, CommitRepository commitRepository, CommitAnalysisRepository commitAnalysisRepository, RepoRepository repositoryRepository, UserRepository userRepository, EntityManager entityManager) {
        this.pullRequestRepository = pullRequestRepository;
        this.commitRepository = commitRepository;
        this.commitAnalysisRepository = commitAnalysisRepository;
        this.repositoryRepository = repositoryRepository;
        this.userRepository = userRepository;
        this.entityManager = entityManager;
    }

    @Override
    public List<PullRequest> get(Long repoId, String branch) {
        return pullRequestRepository.findByBranchAndRepository_Id(branch, repoId);
    }

    @Override
    public List<PullRequest> getByUser(String username) {
        return pullRequestRepository.findByRepository_OwnerLogin(username);
    }

    @Override
    public List<PullRequest> get(Long repoId) {
        return pullRequestRepository.findAllByRepositoryId(repoId);
    }

    @Override
    public PullRequest getById(Long id) {
        return pullRequestRepository.findById(id).orElseThrow(() -> new NotFoundException("prNotFound"));
    }

    @Override
    @Transactional
    public void save(PullRequestWithAnalysisDto model) {
        PullRequest pr = PullRequestMapper.mapToEntity(
                model,
                repositoryRepository,
                entityManager,
                userRepository);
        pullRequestRepository.save(pr);
    }
}

package com.api.devsync.service.impl;

import com.api.devsync.entity.*;
import com.api.devsync.exception.NotFoundException;
import com.api.devsync.model.dto.CommitAnalysisDto;
import com.api.devsync.model.dto.PullRequestAnalysisDto;
import com.api.devsync.model.dto.PullRequestWithAnalysisDto;
import com.api.devsync.model.fromWebhook.Sender;
import com.api.devsync.repository.*;
import com.api.devsync.service.PullRequestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PullRequestServiceImpl implements PullRequestService {
    private final PullRequestRepository pullRequestRepository;
    private final CommitRepository commitRepository;
    private final CommitAnalysisRepository commitAnalysisRepository;
    private final RepoRepository repositoryRepository;
    private final UserRepository userRepository;

    public PullRequestServiceImpl(PullRequestRepository pullRequestRepository, CommitRepository commitRepository, CommitAnalysisRepository commitAnalysisRepository, RepoRepository repositoryRepository, UserRepository userRepository) {
        this.pullRequestRepository = pullRequestRepository;
        this.commitRepository = commitRepository;
        this.commitAnalysisRepository = commitAnalysisRepository;
        this.repositoryRepository = repositoryRepository;
        this.userRepository = userRepository;
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

        PullRequest pr = new PullRequest();
        fillPullRequest(pr, model);
        pullRequestRepository.save(pr);
    }

    private void fillPullRequest(PullRequest pr, PullRequestWithAnalysisDto model) {
        pr.setId(System.currentTimeMillis());
        fillBranchAndPusher(pr, model);
        fillHeadCommit(pr, model);

        List<Commit> commits = fillCommits(model);
        User user = fillUser(model);
        if (user != null) pr.setCreatedBy(user);

        Repository repo = fillRepository(model);
        pr.setRepository(repo);

        setNodesAnalysis(pr, commits, model.getAnalyze());
        pr.setAnalyzedDate(LocalDateTime.now());
    }

    private void fillBranchAndPusher(PullRequest pr, PullRequestWithAnalysisDto model) {
        String[] branchParts = model.getModel().getRef().split("/");
        pr.setBranch(branchParts[branchParts.length - 1]);
        pr.setPusher(model.getModel().getPusher().getName());
    }

    private void fillHeadCommit(PullRequest pr, PullRequestWithAnalysisDto model) {
        if (model.getModel().getHead_commit() != null) {
            pr.setHeadCommitMessage(model.getModel().getHead_commit().getMessage());
            pr.setHeadCommitSha(model.getModel().getHead_commit().getId());
        }
    }

    private List<Commit> fillCommits(PullRequestWithAnalysisDto model) {
        List<Commit> commits = new ArrayList<>();
        if (model.getModel().getCommits() != null) {
            for (com.api.devsync.model.fromWebhook.Commit c : model.getModel().getCommits()) {
                Commit commit = commitRepository.findById(c.getId())
                        .map(existing -> {
                            existing.setMessage(c.getMessage());
                            return existing;
                        })
                        .orElseGet(() -> new Commit(c.getId(), c.getMessage()));
                commits.add(commit);
            }
        }
        return commits;
    }

    private User fillUser(PullRequestWithAnalysisDto model) {
        if (model.getModel().getSender() == null) return null;

        Sender sender = model.getModel().getSender();
        User user = userRepository.findById(sender.getId())
                .orElseGet(() -> {
                    User u = new User();
                    u.setGithubId(sender.getId());
                    return userRepository.save(u);
                });
        user.setUsername(sender.getLogin());
        user.setAvatarUrl(sender.getAvatar_url());
        user.setUserType(sender.getType());
        return user;
    }

    private Repository fillRepository(PullRequestWithAnalysisDto model) {
        com.api.devsync.model.fromWebhook.Repository repoDto = model.getModel().getRepository();
        Repository repo = repositoryRepository.findById(repoDto.getId())
                .orElseGet(() -> {
                    Repository r = new Repository();
                    r.setId(repoDto.getId());
                    return repositoryRepository.save(r);
                });
        repo.setName(repoDto.getName());
        repo.setFullName(repoDto.getFull_name());
        repo.setHtmlUrl(repoDto.getHtml_url());
        repo.setVisibility(repoDto.getVisibility());
        repo.setLanguage(repoDto.getLanguage());
        repo.setDescription(repoDto.getDescription());
        repo.setDefaultBranch(repoDto.getDefault_branch());
        repo.setOwnerLogin(repoDto.getOwner().getLogin());
        repo.setOwnerId(repoDto.getOwner().getId());
        return repo;
    }


    private void setNodesAnalysis(PullRequest pr, List<Commit> commits, PullRequestAnalysisDto analyzeDto) {
        if (analyzeDto == null) return;

        PullRequestAnalysis prAnalysis = new PullRequestAnalysis();
        prAnalysis.setFunctionalComment(analyzeDto.getFunctionalComment());
        prAnalysis.setRiskScore(analyzeDto.getRiskScore());
        prAnalysis.setArchitecturalComment(analyzeDto.getArchitecturalComment());
        prAnalysis.setTechnicalComment(analyzeDto.getTechnicalComment());
        pr.setAnalysis(prAnalysis);

        Map<String, CommitAnalysisDto> commitAnalysisMap = analyzeDto.getCommitAnalysis()
                .stream()
                .collect(Collectors.toMap(CommitAnalysisDto::getId, Function.identity()));

        for (Commit commit : commits) {
            CommitAnalysisDto analysisDto = commitAnalysisMap.get(commit.getHash());
            if (analysisDto != null) {
                CommitAnalysis analysis = commitAnalysisRepository.findById(analysisDto.getId())
                        .map(existing -> {
                            existing.setRiskScore(analysisDto.getRiskScore());
                            existing.setTechnicalComment(analysisDto.getTechnicalComment());
                            existing.setArchitecturalComment(analysisDto.getArchitecturalComment());
                            existing.setFunctionalComment(analysisDto.getFunctionalComment());
                            existing.setCommit(commit);
                            commit.setAnalysis(existing);
                            return existing;
                        })
                        .orElseGet(() -> {
                            CommitAnalysis ca = new CommitAnalysis();
                            ca.setId(UUID.randomUUID().toString());
                            ca.setCommit(commit);
                            commit.setAnalysis(ca);
                            ca.setRiskScore(analysisDto.getRiskScore());
                            ca.setTechnicalComment(analysisDto.getTechnicalComment());
                            ca.setArchitecturalComment(analysisDto.getArchitecturalComment());
                            ca.setFunctionalComment(analysisDto.getFunctionalComment());
                            return ca;
                        });
                commit.setAnalysis(analysis);
            }
        }
        pr.setCommits(commits);
    }


}

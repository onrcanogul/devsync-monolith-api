package com.api.devsync.service.impl;

import com.api.devsync.entity.*;
import com.api.devsync.exception.NotFoundException;
import com.api.devsync.model.dto.CommitAnalysisDto;
import com.api.devsync.model.dto.PullRequestAnalysisDto;
import com.api.devsync.model.dto.PullRequestWithAnalysisDto;
import com.api.devsync.model.fromWebhook.Sender;
import com.api.devsync.repository.CommitAnalysisRepository;
import com.api.devsync.repository.CommitRepository;
import com.api.devsync.repository.PullRequestRepository;
import com.api.devsync.service.PullRequestService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PullRequestServiceImpl implements PullRequestService {
    private final PullRequestRepository pullRequestRepository;
    private final CommitRepository commitRepository;
    private final CommitAnalysisRepository commitAnalysisRepository;

    public PullRequestServiceImpl(PullRequestRepository pullRequestRepository, CommitRepository commitRepository, CommitAnalysisRepository commitAnalysisRepository) {
        this.pullRequestRepository = pullRequestRepository;
        this.commitRepository = commitRepository;
        this.commitAnalysisRepository = commitAnalysisRepository;
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
    public PullRequest saveFromPR(PullRequestWithAnalysisDto model) {
        PullRequest pr = new PullRequest();
        fillPrNode(pr, model);
        return pullRequestRepository.save(pr);
    }

    private void fillPrNode(PullRequest pr, PullRequestWithAnalysisDto dto) {
        pr.setId(System.currentTimeMillis());

        String[] branchParts = dto.getModel().getRef().split("/");
        String branch = branchParts[branchParts.length - 1];
        pr.setBranch(branch);
        pr.setPusher(dto.getModel().getPusher().getName());

        if (dto.getModel().getHead_commit() != null) {
            pr.setHeadCommitMessage(dto.getModel().getHead_commit().getMessage());
            pr.setHeadCommitSha(dto.getModel().getHead_commit().getId());
        }

        pr.setCommitCount(dto.getModel().getCommits() != null ? dto.getModel().getCommits().size() : 0);

        if (dto.getModel().getCommits() != null) {
            List<Commit> commits = dto.getModel().getCommits().stream()
                    .map(c -> commitRepository.findById(c.getId())
                            .map(existing -> {
                                existing.setMessage(c.getMessage());
                                return existing;
                            })
                            .orElseGet(() -> new Commit(c.getId(), c.getMessage()))
                    )
                    .toList();
            pr.setCommits(commits);
        }

        if (dto.getModel().getSender() != null) {
            User user = new User();
            Sender sender = dto.getModel().getSender();
            user.setGithubId(sender.getId());
            user.setUsername(sender.getLogin());
            user.setAvatarUrl(sender.getAvatar_url());
            user.setUserType(sender.getType());
            pr.setCreatedBy(user);
        }

        Repository repository = getRepository(dto);
        pr.setRepository(repository);

        setNodesAnalysis(pr, dto);
    }


    private static Repository getRepository(PullRequestWithAnalysisDto dto) {
        Repository repository = new Repository();
        repository.setId(dto.getModel().getRepository().getId());
        repository.setName(dto.getModel().getRepository().getName());
        repository.setFullName(dto.getModel().getRepository().getFull_name());
        repository.setHtmlUrl(dto.getModel().getRepository().getHtml_url());
        repository.setVisibility(dto.getModel().getRepository().getVisibility());
        repository.setLanguage(dto.getModel().getRepository().getLanguage());
        repository.setDescription(dto.getModel().getRepository().getDescription());
        repository.setDefaultBranch(dto.getModel().getRepository().getDefault_branch());
        repository.setOwnerLogin(dto.getModel().getRepository().getName());
        repository.setOwnerId(dto.getModel().getRepository().getId());
        return repository;
    }

    private void setNodesAnalysis(PullRequest pr, PullRequestWithAnalysisDto model) {
        PullRequestAnalysisDto analyzeDto = model.getAnalyze();
        if (analyzeDto == null) return;

        PullRequestAnalysis pullRequestAnalysis = new PullRequestAnalysis();
        pullRequestAnalysis.setFunctionalComment(analyzeDto.getFunctionalComment());
        pullRequestAnalysis.setRiskScore(analyzeDto.getRiskScore());
        pullRequestAnalysis.setArchitecturalComment(analyzeDto.getArchitecturalComment());
        pullRequestAnalysis.setTechnicalComment(analyzeDto.getTechnicalComment());
        pr.setAnalysis(pullRequestAnalysis);

        Map<String, CommitAnalysisDto> commitAnalysisMap = analyzeDto.getCommitAnalysis()
                .stream()
                .collect(Collectors.toMap(CommitAnalysisDto::getHash, Function.identity()));

        pr.getCommits().forEach(commitNode -> {
            CommitAnalysisDto analysis = commitAnalysisMap.get(commitNode.getHash());
            if (analysis != null) {
                CommitAnalysis commitAnalysis = commitAnalysisRepository.findByHash(analysis.getHash())
                        .map(existing -> {
                            existing.setRiskScore(analysis.getRiskScore());
                            existing.setTechnicalComment(analysis.getTechnicalComment());
                            existing.setArchitecturalComment(analysis.getArchitecturalComment());
                            existing.setFunctionalComment(analysis.getFunctionalComment());
                            return existing;
                        })
                        .orElseGet(() -> {
                            CommitAnalysis ca = new CommitAnalysis();
                            ca.setHash(analysis.getHash());
                            ca.setRiskScore(analysis.getRiskScore());
                            ca.setTechnicalComment(analysis.getTechnicalComment());
                            ca.setArchitecturalComment(analysis.getArchitecturalComment());
                            ca.setFunctionalComment(analysis.getFunctionalComment());
                            return ca;
                        });

                commitNode.setAnalysis(commitAnalysis);
            }
        });
    }

}

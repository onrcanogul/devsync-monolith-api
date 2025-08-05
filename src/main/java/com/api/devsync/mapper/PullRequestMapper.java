package com.api.devsync.mapper;


import com.api.devsync.entity.*;
import com.api.devsync.model.dto.CommitAnalysisDto;
import com.api.devsync.model.dto.PullRequestAnalysisDto;
import com.api.devsync.model.dto.PullRequestWithAnalysisDto;
import com.api.devsync.repository.CommitAnalysisRepository;
import com.api.devsync.repository.CommitRepository;
import com.api.devsync.repository.RepoRepository;
import com.api.devsync.repository.UserRepository;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PullRequestMapper {

    public static PullRequest mapToEntity(
            PullRequestWithAnalysisDto dto,
            CommitRepository commitRepo,
            CommitAnalysisRepository commitAnalysisRepo,
            RepoRepository repoRepo,
            UserRepository userRepo
    ) {
        PullRequest pr = new PullRequest();

        pr.setId(System.currentTimeMillis());
        pr.setBranch(extractBranch(dto));
        pr.setPusher(dto.getModel().getPusher().getName());

        if (dto.getModel().getHead_commit() != null) {
            pr.setHeadCommitMessage(dto.getModel().getHead_commit().getMessage());
            pr.setHeadCommitSha(dto.getModel().getHead_commit().getId());
        }

        User user = mapUser(dto, userRepo);
        if (user != null) pr.setCreatedBy(user);

        Repository repo = mapRepository(dto, repoRepo);
        pr.setRepository(repo);

        List<Commit> commits = mapCommits(dto, commitRepo);
        pr.setCommits(commits);

        if (dto.getAnalyze() != null) {
            mapAnalysis(pr, commits, dto.getAnalyze(), commitAnalysisRepo);
        }

        pr.setAnalyzedDate(LocalDateTime.now());

        return pr;
    }

    private static String extractBranch(PullRequestWithAnalysisDto dto) {
        String[] branchParts = dto.getModel().getRef().split("/");
        return branchParts[branchParts.length - 1];
    }

    private static User mapUser(PullRequestWithAnalysisDto dto, UserRepository userRepo) {
        var sender = dto.getModel().getSender();
        if (sender == null) return null;

        return userRepo.findById(sender.getId())
                .map(existing -> {
                    existing.setUsername(sender.getLogin());
                    existing.setAvatarUrl(sender.getAvatar_url());
                    existing.setUserType(sender.getType());
                    return existing;
                })
                .orElseGet(() -> {
                    User u = new User();
                    u.setGithubId(sender.getId());
                    u.setUsername(sender.getLogin());
                    u.setAvatarUrl(sender.getAvatar_url());
                    u.setUserType(sender.getType());
                    return userRepo.save(u);
                });
    }

    private static Repository mapRepository(PullRequestWithAnalysisDto dto, RepoRepository repoRepo) {
        var repoDto = dto.getModel().getRepository();
        return repoRepo.findById(repoDto.getId())
                .map(existing -> {
                    existing.setName(repoDto.getName());
                    existing.setFullName(repoDto.getFull_name());
                    existing.setHtmlUrl(repoDto.getHtml_url());
                    existing.setVisibility(repoDto.getVisibility());
                    existing.setLanguage(repoDto.getLanguage());
                    existing.setDescription(repoDto.getDescription());
                    existing.setDefaultBranch(repoDto.getDefault_branch());
                    existing.setOwnerLogin(repoDto.getOwner().getLogin());
                    existing.setOwnerId(repoDto.getOwner().getId());
                    return existing;
                })
                .orElseGet(() -> {
                    Repository r = new Repository();
                    r.setId(repoDto.getId());
                    r.setName(repoDto.getName());
                    r.setFullName(repoDto.getFull_name());
                    r.setHtmlUrl(repoDto.getHtml_url());
                    r.setVisibility(repoDto.getVisibility());
                    r.setLanguage(repoDto.getLanguage());
                    r.setDescription(repoDto.getDescription());
                    r.setDefaultBranch(repoDto.getDefault_branch());
                    r.setOwnerLogin(repoDto.getOwner().getLogin());
                    r.setOwnerId(repoDto.getOwner().getId());
                    return repoRepo.save(r);
                });
    }

    private static List<Commit> mapCommits(PullRequestWithAnalysisDto dto, CommitRepository commitRepo) {
        List<Commit> commits = new ArrayList<>();
        if (dto.getModel().getCommits() == null) return commits;

        for (var c : dto.getModel().getCommits()) {
            Commit commit = commitRepo.findById(c.getId())
                    .map(existing -> {
                        existing.setMessage(c.getMessage());
                        return existing;
                    })
                    .orElseGet(() -> new Commit(c.getId(), c.getMessage()));
            commits.add(commit);
        }
        return commits;
    }

    private static void mapAnalysis(PullRequest pr, List<Commit> commits, PullRequestAnalysisDto analysisDto, CommitAnalysisRepository commitAnalysisRepo) {
        PullRequestAnalysis prAnalysis = new PullRequestAnalysis();
        prAnalysis.setFunctionalComment(analysisDto.getFunctionalComment());
        prAnalysis.setRiskScore(analysisDto.getRiskScore());
        prAnalysis.setArchitecturalComment(analysisDto.getArchitecturalComment());
        prAnalysis.setTechnicalComment(analysisDto.getTechnicalComment());
        pr.setAnalysis(prAnalysis);

        Map<String, CommitAnalysisDto> commitAnalysisMap = analysisDto.getCommitAnalysis()
                .stream()
                .collect(Collectors.toMap(CommitAnalysisDto::getId, Function.identity()));

        for (Commit commit : commits) {
            CommitAnalysisDto dto = commitAnalysisMap.get(commit.getHash());
            if (dto == null) continue;

            CommitAnalysis analysis = commitAnalysisRepo.findById(dto.getId())
                    .map(existing -> {
                        existing.setRiskScore(dto.getRiskScore());
                        existing.setTechnicalComment(dto.getTechnicalComment());
                        existing.setArchitecturalComment(dto.getArchitecturalComment());
                        existing.setFunctionalComment(dto.getFunctionalComment());
                        existing.setCommit(commit);
                        return existing;
                    })
                    .orElseGet(() -> {
                        CommitAnalysis ca = new CommitAnalysis();
                        ca.setId(dto.getId());
                        ca.setCommit(commit);
                        ca.setRiskScore(dto.getRiskScore());
                        ca.setTechnicalComment(dto.getTechnicalComment());
                        ca.setArchitecturalComment(dto.getArchitecturalComment());
                        ca.setFunctionalComment(dto.getFunctionalComment());
                        return ca;
                    });

            commit.setAnalysis(analysis);
        }
    }
}


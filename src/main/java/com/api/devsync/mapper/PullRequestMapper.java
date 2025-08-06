package com.api.devsync.mapper;

import com.api.devsync.entity.*;
import com.api.devsync.exception.BadRequestException;
import com.api.devsync.exception.NotFoundException;
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
import java.util.*;
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
        pr.setCommits(new ArrayList<>());
        pr.setId(System.currentTimeMillis());
        pr.setBranch(extractBranch(dto));
        pr.setPusher(dto.getModel().getPusher().getName());

        User user = mapUser(dto, userRepo);
        Repository repo = mapRepository(dto, repoRepo);
        mapCommits(dto, pr, commitRepo);

        pr.setCreatedBy(user);
        pr.setRepository(repo);

        pr.setAnalysis(dto.getAnalyze());
        pr.setAnalyzedDate(LocalDateTime.now());
        pr.setHeadCommitMessage(dto.getModel().getHead_commit().getMessage());
        pr.setHeadCommitSha(dto.getModel().getHead_commit().getId());
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

    private static void mapCommits(PullRequestWithAnalysisDto dto, PullRequest pullRequest, CommitRepository commitRepository) {
        if (dto.getModel().getCommits() == null) return;

        for (var c : dto.getModel().getCommits()) {
            Commit newCommit = new Commit(c.getId(), c.getMessage());
            newCommit.setPullRequest(pullRequest);
            pullRequest.getCommits().add(newCommit);
            CommitAnalysis commitAnalysis = dto.getAnalyze()
                    .getCommitAnalysis()
                    .stream()
                    .filter(ca -> Objects.equals(ca.getId(), c.getId()))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("commitHashNotFound"));
            commitRepository.save(newCommit);
            commitAnalysis.setCommit(newCommit);
            newCommit.setAnalysis(commitAnalysis);
        }
    }

}


package com.api.devsync.mapper.custom;

import com.api.devsync.entity.PullRequestAnalysis;
import com.api.devsync.model.fromWebhook.GithubWebhookModel;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CustomPullRequestAnalyzeMapper {

    public PullRequestAnalysis mapFromDto(GithubWebhookModel dto) {
        PullRequestAnalysis analyze = new PullRequestAnalysis();

        analyze.setCommitCount(dto.getCommits().size());
        analyze.setFileChangeCount(dto.getHead_commit().getModified().size());
        analyze.setAnalyzedAt(LocalDateTime.now());

        String[] refParts = dto.getRef().split("/");
        String branchName = refParts[refParts.length - 1];
        analyze.setBranch(branchName);

        analyze.setRepoId(dto.getRepository().getId());
        analyze.setRepoName(dto.getRepository().getName());

        int additions = dto.getCommits().stream()
                .flatMap(commit -> commit.getModified().stream())
                .mapToInt(file -> 1)
                .sum();
        analyze.setTotalAdditions(additions);

        int deletions = dto.getCommits().stream()
                .flatMap(commit -> commit.getRemoved().stream())
                .mapToInt(file -> 1)
                .sum();
        analyze.setTotalDeletions(deletions);
        return analyze;
    }

}

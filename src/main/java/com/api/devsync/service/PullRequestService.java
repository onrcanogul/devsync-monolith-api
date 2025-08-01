package com.api.devsync.service;

import com.api.devsync.entity.PullRequest;
import com.api.devsync.model.dto.PullRequestWithAnalysisDto;

import java.util.List;

public interface PullRequestService {
    List<PullRequest> get(Long repoId, String branch);
    List<PullRequest> getByUser(String username);
    List<PullRequest> get(Long repoId);
    PullRequest getById(Long id);
    PullRequest saveFromPR(PullRequestWithAnalysisDto model);
}

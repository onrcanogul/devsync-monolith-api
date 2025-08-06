package com.api.devsync.model.dto;

import com.api.devsync.entity.PullRequestAnalysis;
import com.api.devsync.model.viewmodel.fromWebhook.GithubWebhookModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PullRequestWithAnalysisDto {
    private GithubWebhookModel model;
    private PullRequestAnalysis analyze;
}

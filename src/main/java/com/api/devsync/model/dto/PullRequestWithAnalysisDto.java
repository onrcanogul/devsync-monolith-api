package com.api.devsync.model.dto;

import com.api.devsync.model.fromWebhook.GithubWebhookModel;
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
    private PullRequestAnalysisDto analyze;
}

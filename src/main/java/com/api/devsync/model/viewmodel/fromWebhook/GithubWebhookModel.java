package com.api.devsync.model.viewmodel.fromWebhook;

import lombok.Data;

import java.util.List;

@Data
public class GithubWebhookModel {
    private String ref;
    private String before;
    private String after;
    private RepositoryFromWebhook repository;
    private PusherFromWebhook pusher;
    private SenderFromWebhook sender;
    private boolean created;
    private boolean deleted;
    private boolean forced;
    private String base_ref;
    private String compare;
    private List<CommitFromWebhook> commit;
    private HeadCommitFromWebhook head_commit;
}

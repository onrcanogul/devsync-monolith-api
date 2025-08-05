package com.api.devsync.model.viewmodel.fromWebhook;

import lombok.Data;

import java.util.List;

@Data
public class GithubWebhookModel {
    private String ref;
    private String before;
    private String after;
    private Repository repository;
    private Pusher pusher;
    private Sender sender;
    private boolean created;
    private boolean deleted;
    private boolean forced;
    private String base_ref;
    private String compare;
    private List<Commit> commits;
    private HeadCommit head_commit;
}

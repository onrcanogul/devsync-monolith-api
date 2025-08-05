package com.api.devsync.model.viewmodel.fromWebhook;

import lombok.Data;

import java.util.List;

@Data
public class HeadCommitFromWebhook {
    private String id;
    private String tree_id;
    private boolean distinct;
    private String message;
    private String timestamp;
    private String url;
    private AuthorFromWebhook author;
    private CommitterFromWebhook committer;
    private List<String> added;
    private List<String> removed;
    private List<String> modified;
}
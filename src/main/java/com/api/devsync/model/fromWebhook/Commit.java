package com.api.devsync.model.fromWebhook;

import lombok.Data;

import java.util.List;

@Data
public class Commit {
    private String id;
    private String tree_id;
    private boolean distinct;
    private String message;
    private String timestamp;
    private String url;
    private Author author;
    private Committer committer;
    private List<String> added;
    private List<String> removed;
    private List<String> modified;
}

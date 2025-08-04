package com.api.devsync.model.fromApi.repository;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class RepositoryFromApi {
    private long id;
    @JsonProperty("node_id")
    private String nodeId;
    private String name;
    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("private")
    private boolean isPrivate;
    private OwnerFromApi owner;
    @JsonProperty("html_url")
    private String htmlUrl;
    private String description;
    private boolean fork;
    private String url;
    @JsonProperty("created_at")
    private OffsetDateTime createdAt;
    @JsonProperty("updated_at")
    private OffsetDateTime updatedAt;
    @JsonProperty("pushed_at")
    private OffsetDateTime pushedAt;
    @JsonProperty("git_url")
    private String gitUrl;
    @JsonProperty("ssh_url")
    private String sshUrl;
    @JsonProperty("clone_url")
    private String cloneUrl;
    @JsonProperty("svn_url")
    private String svnUrl;
    private String homepage;
    private int size;
    @JsonProperty("stargazers_count")
    private int stargazersCount;
    @JsonProperty("watchers_count")
    private int watchersCount;
    private String language;
    @JsonProperty("has_issues")
    private boolean hasIssues;
    @JsonProperty("has_projects")
    private boolean hasProjects;
    @JsonProperty("has_downloads")
    private boolean hasDownloads;
    @JsonProperty("has_wiki")
    private boolean hasWiki;
    @JsonProperty("has_pages")
    private boolean hasPages;
    @JsonProperty("has_discussions")
    private boolean hasDiscussions;
    @JsonProperty("forks_count")
    private int forksCount;
    private boolean archived;
    private boolean disabled;
    @JsonProperty("open_issues_count")
    private int openIssuesCount;
    private LicenseFromApi license;
    @JsonProperty("allow_forking")
    private boolean allowForking;
    @JsonProperty("is_template")
    private boolean isTemplate;
    @JsonProperty("web_commit_signoff_required")
    private boolean webCommitSignoffRequired;
    private String[] topics;
    private String visibility;
    private int forks;
    @JsonProperty("open_issues")
    private int openIssues;
    private int watchers;
    @JsonProperty("default_branch")
    private String defaultBranch;
    private int stargazers;
    @JsonProperty("master_branch")
    private String masterBranch;
    private boolean hasTargetWebhook;
}


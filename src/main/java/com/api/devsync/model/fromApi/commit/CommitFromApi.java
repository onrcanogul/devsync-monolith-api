package com.api.devsync.model.fromApi.commit;

import lombok.Data;

@Data
public class CommitFromApi {
    private CommitAuthorFromApi author;
    private CommitterFromApi committer;
    private String message;
    private String url;
    private int comment_count;
}

package com.api.devsync.model.viewmodel.fromApi.commit;

import lombok.Data;

@Data
public class CommitDetailFromApi {
    private CommitAuthorFromApi author;
    private CommitterFromApi committer;
    private String message;
    private String url;
    private int comment_count;
    private CommitVerificationFromApi verification;
}

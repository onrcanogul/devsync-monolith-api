package com.api.devsync.model.fromApi.commit;

import lombok.Data;

@Data
public class ParentCommitFromApi {
    private String sha;
    private String url;
    private String html_url;
}

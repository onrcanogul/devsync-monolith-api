package com.api.devsync.model.fromApi.commit;

import lombok.Data;

@Data
public class CommitterFromApi {
    private String login;
    private long id;
    private String avatar_url;
    private String html_url;
}

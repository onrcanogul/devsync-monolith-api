package com.api.devsync.model.fromApi.commit;

import lombok.Data;

@Data
public class CommitAuthorFromApi {
    private String name;
    private String email;
    private String date;
}

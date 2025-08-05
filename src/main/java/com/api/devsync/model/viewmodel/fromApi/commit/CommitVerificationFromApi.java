package com.api.devsync.model.viewmodel.fromApi.commit;

import lombok.Data;

@Data
public class CommitVerificationFromApi {
    private boolean verified;
    private String reason;
    private String signature;
    private String payload;
    private String verified_at;
}

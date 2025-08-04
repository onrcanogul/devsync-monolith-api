package com.api.devsync.model.fromApi.commit;

import lombok.Data;

@Data
public class CommitStats {
    private int total;
    private int additions;
    private int deletions;
}

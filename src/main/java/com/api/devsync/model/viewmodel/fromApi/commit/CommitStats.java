package com.api.devsync.model.viewmodel.fromApi.commit;

import lombok.Data;

@Data
public class CommitStats {
    private int total;
    private int additions;
    private int deletions;
}

package com.api.devsync.model.viewmodel.fromApi.commit;

import lombok.Data;

@Data
public class CommitCommitter {
    private String name;
    private String email;
    private String date;
}
package com.api.devsync.model.viewmodel.fromApi.commit;

import lombok.Data;

@Data
public class FileChangeFromApi {
    private String sha;
    private String filename;
    private String status;
    private int additions;
    private int deletions;
    private int changes;
    private String patch;
}

package com.api.devsync.model.viewmodel.fromApi.commit;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CommitResponseFromApi {
     private String sha;
     private CommitFromApi commit;
     private List<FileChangeFromApi> files;
     // min ai billing :)
     // private String node_id;
     // private List<ParentCommitFromApi> parents;
     // private CommitStats stats;
}

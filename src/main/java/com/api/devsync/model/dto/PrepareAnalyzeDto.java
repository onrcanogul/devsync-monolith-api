package com.api.devsync.model.dto;

import com.api.devsync.model.viewmodel.fromApi.commit.CommitResponseFromApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrepareAnalyzeDto {
    private String repositoryName;
    private String fullName;
    private String repositoryDescription;
    private String branchName;
    private Long repoId;
    private String author;
    private String commitMessage;
    private List<CommitResponseFromApi> commits = new ArrayList<>();
}

package com.api.devsync.mapper;

import com.api.devsync.entity.PullRequestAnalysis;
import com.api.devsync.model.dto.PullRequestAnalysisDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PullRequestAnalyzeMapper {
    PullRequestAnalysisDto toDto(PullRequestAnalysis entity);
    PullRequestAnalysis toEntity(PullRequestAnalysisDto dto);
}

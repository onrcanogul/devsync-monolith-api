package com.api.devsync.mapper;


import com.api.devsync.entity.Analyze;
import com.api.devsync.model.dto.AnalyzeDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AnalyzeMapper {
    AnalyzeDto toDto(Analyze entity);
    Analyze toEntity(AnalyzeDto dto);
}

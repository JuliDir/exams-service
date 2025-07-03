// Updated ExamMapper.java
package com.eximia.exams.mapper;

import com.eximia.exams.domain.entities.Exam;
import com.eximia.exams.dto.request.ExamRequestDto;
import com.eximia.exams.dto.response.ExamResponseDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ExamMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Exam createEntity(ExamRequestDto requestDto);

    @Mapping(target = "questions", ignore = true)
    ExamResponseDto toResponseDto(Exam exam);

    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateEntity(@MappingTarget Exam exam, ExamRequestDto requestDto);
}
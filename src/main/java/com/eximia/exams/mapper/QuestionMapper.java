package com.eximia.exams.mapper;

import com.eximia.exams.domain.entities.Question;
import com.eximia.exams.dto.request.QuestionRequestDto;
import com.eximia.exams.dto.response.QuestionResponseDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface QuestionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Question toEntity(QuestionRequestDto requestDto);

    QuestionResponseDto toResponseDto(Question question);

    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateEntity(@MappingTarget Question question, QuestionRequestDto requestDto);
}
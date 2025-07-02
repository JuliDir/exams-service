package com.eximia.exams.mapper;

import com.eximia.exams.domain.entities.Question;
import com.eximia.exams.dto.request.QuestionRequestDto;
import com.eximia.exams.dto.response.QuestionResponseDto;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {OptionMapper.class}
)
public interface QuestionMapper {

    @Mapping(target = "questionId", expression = "java(java.util.UUID.randomUUID().toString())")
    Question toEntity(QuestionRequestDto requestDto);

    QuestionResponseDto toResponseDto(Question question);
}

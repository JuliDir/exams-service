package com.eximia.exams.mapper;

import com.eximia.exams.domain.entities.Option;
import com.eximia.exams.dto.request.OptionRequestDto;
import com.eximia.exams.dto.response.OptionResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OptionMapper {

    @Mapping(target = "optionId", expression = "java(java.util.UUID.randomUUID().toString())")
    Option toEntity(OptionRequestDto requestDto);

    OptionResponseDto toResponseDto(Option option);
}

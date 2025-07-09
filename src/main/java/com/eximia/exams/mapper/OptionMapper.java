package com.eximia.exams.mapper;

import com.eximia.exams.domain.entities.Option;
import com.eximia.exams.dto.request.OptionRequestDto;
import com.eximia.exams.dto.response.OptionResponseDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface OptionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Option toEntity(OptionRequestDto requestDto);

    OptionResponseDto toResponseDto(Option option);

    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateEntity(@MappingTarget Option option, OptionRequestDto requestDto);
}
package com.eximia.exams.service;

import com.eximia.exams.dto.request.OptionRequestDto;
import com.eximia.exams.dto.response.OptionResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OptionService {

    OptionResponseDto createOption(String questionId, OptionRequestDto optionRequestDto);

    OptionResponseDto getOptionById(String id);

    List<OptionResponseDto> getOptionsByQuestionId(String questionId);

    OptionResponseDto updateOption(String id, OptionRequestDto optionRequestDto);

    void deleteOption(String id);

    void deleteOptionsByQuestionId(String questionId);

    void deleteOptionsByQuestionIds(List<String> questionIds);

}
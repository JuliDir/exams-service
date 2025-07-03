package com.eximia.exams.service;

import com.eximia.exams.dto.request.OptionRequestDto;
import com.eximia.exams.dto.response.OptionResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OptionService {

    OptionResponseDto createOption(String questionId, OptionRequestDto optionRequestDto);

    OptionResponseDto getOptionById(String id);

    OptionResponseDto getOptionByIdAndQuestionId(String id, String questionId);

    List<OptionResponseDto> getOptionsByQuestionId(String questionId);

    Page<OptionResponseDto> getOptionsByQuestionId(String questionId, Pageable pageable);

    List<OptionResponseDto> getOptionsByCreator(String createdBy);

    List<OptionResponseDto> getCorrectOptionsByQuestionId(String questionId);

    List<OptionResponseDto> getIncorrectOptionsByQuestionId(String questionId);

    List<OptionResponseDto> searchOptionsByText(String optionText);

    List<OptionResponseDto> getOptionsByPointsRange(Double minPoints, Double maxPoints);

    OptionResponseDto updateOption(String id, OptionRequestDto optionRequestDto);

    OptionResponseDto updateOptionInQuestion(String id, String questionId, OptionRequestDto optionRequestDto);

    void deleteOption(String id);

    void deleteOptionFromQuestion(String id, String questionId);

    void deleteOptionsByQuestionId(String questionId);

    void deleteOptionsByQuestionIds(List<String> questionIds);

    long countOptionsByQuestionId(String questionId);

    long countCorrectOptionsByQuestionId(String questionId);

    long countIncorrectOptionsByQuestionId(String questionId);

    void validateOptionBelongsToQuestion(String optionId, String questionId);
}
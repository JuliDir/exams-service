package com.eximia.exams.service.impl;

import com.eximia.exams.domain.enums.QuestionType;
import com.eximia.exams.dto.request.OptionRequestDto;
import com.eximia.exams.dto.request.QuestionRequestDto;
import com.eximia.exams.exception.CustomException;
import com.eximia.exams.service.QuestionValidationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MultipleSelectionValidationStrategy implements QuestionValidationStrategy {

    @Override
    public QuestionType getSupportedType() {
        return QuestionType.MULTIPLE_SELECTION;
    }

    @Override
    public void validate(QuestionRequestDto questionRequestDto) {
        List<OptionRequestDto> options = questionRequestDto.getOptions();

        long correctCount = options.stream()
                .filter(OptionRequestDto::getIsCorrect)
                .count();

        if (correctCount < 2) {
            throw new CustomException(
                    "Multiple selection question must have at least two correct options, found " + correctCount
            );
        }

        if (options.size() < 3) {
            throw new CustomException(
                    "Multiple selection question must have at least three options total, found " + options.size()
            );
        }
    }
}
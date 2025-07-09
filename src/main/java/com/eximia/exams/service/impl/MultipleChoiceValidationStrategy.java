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
public class MultipleChoiceValidationStrategy implements QuestionValidationStrategy {

    @Override
    public QuestionType getSupportedType() {
        return QuestionType.MULTIPLE_CHOICE;
    }

    @Override
    public void validate(QuestionRequestDto questionRequestDto) {
        List<OptionRequestDto> options = questionRequestDto.getOptions();

        long correctCount = options.stream()
                .filter(OptionRequestDto::getIsCorrect)
                .count();

        if (correctCount != 1) {
            throw new CustomException(
                    String.format("Multiple choice question must have exactly one correct option, found %d", correctCount)
            );
        }
    }
}
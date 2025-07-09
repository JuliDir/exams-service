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
public class TrueFalseValidationStrategy implements QuestionValidationStrategy {

    @Override
    public QuestionType getSupportedType() {
        return QuestionType.TRUE_FALSE;
    }

    @Override
    public void validate(QuestionRequestDto questionRequestDto) {
        List<OptionRequestDto> options = questionRequestDto.getOptions();

        if (options.size() != 1) {
            throw new CustomException(
                    "True/False question must have exactly one option, found " + options.size()
            );
        }

        if (options.getFirst().getIsCorrect() == null) {
            throw new CustomException(
                    "True/False question must have exactly one correct or incorrect option"
            );
        }
    }
}

package com.eximia.exams.service.impl;

import com.eximia.exams.domain.entities.Question;
import com.eximia.exams.domain.enums.QuestionType;
import com.eximia.exams.dto.response.OptionResponseDto;
import com.eximia.exams.exception.CustomException;
import com.eximia.exams.service.OptionService;
import com.eximia.exams.service.QuestionValidationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TrueFalseValidationStrategy implements QuestionValidationStrategy {

    private final OptionService optionService;

    @Override
    public QuestionType getSupportedType() {
        return QuestionType.TRUE_FALSE;
    }

    @Override
    public void validate(Question question) {
        List<OptionResponseDto> options = optionService.getOptionsByQuestionId(question.getId());

        if (options.size() != 2) {
            throw new CustomException(
                    "True/False question must have exactly two options, found " + options.size()
            );
        }

        // Validate that one option is true and one is false
        long correctCount = options.stream()
                .filter(OptionResponseDto::getIsCorrect)
                .count();

        if (correctCount != 1) {
            throw new CustomException(
                    "True/False question must have exactly one correct option"
            );
        }

        // Validate points match
        double sumPoints = options.stream()
                .mapToDouble(OptionResponseDto::getPoints)
                .sum();

        if (Double.compare(sumPoints, question.getPoints()) != 0) {
            throw new CustomException(
                    String.format("Total points of options (%.2f) must match question points (%.2f)",
                            sumPoints, question.getPoints())
            );
        }
    }
}

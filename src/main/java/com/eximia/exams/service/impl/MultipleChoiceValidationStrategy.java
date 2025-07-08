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
public class MultipleChoiceValidationStrategy implements QuestionValidationStrategy {

    private final OptionService optionService;

    @Override
    public QuestionType getSupportedType() {
        return QuestionType.MULTIPLE_CHOICE;
    }

    @Override
    public void validate(Question question) {
        List<OptionResponseDto> options = optionService.getOptionsByQuestionId(question.getId());

        long correctCount = options.stream()
                .filter(OptionResponseDto::getIsCorrect)
                .count();

        if (correctCount != 1) {
            throw new CustomException(
                    String.format("Multiple choice question must have exactly one correct option, found %d", correctCount)
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
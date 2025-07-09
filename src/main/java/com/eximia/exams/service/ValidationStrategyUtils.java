package com.eximia.exams.service;

import com.eximia.exams.domain.entities.Question;
import com.eximia.exams.dto.response.OptionResponseDto;
import com.eximia.exams.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ValidationStrategyUtils {

    private static OptionService optionService;

    @Autowired
    public void setOptionService(OptionService optionService) {
        ValidationStrategyUtils.optionService = optionService;
    }

    public static void validatePointsMatch(Question question) {
        if (question.getOptionIds() == null || question.getOptionIds().isEmpty()) {
            throw new CustomException("Question must have at least one option");
        }

        List<OptionResponseDto> options = optionService.getOptionsByQuestionId(question.getId());

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
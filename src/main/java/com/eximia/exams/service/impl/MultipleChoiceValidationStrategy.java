package com.eximia.exams.service.impl;

import com.eximia.exams.domain.entities.Option;
import com.eximia.exams.domain.entities.Question;
import com.eximia.exams.domain.enums.QuestionType;
import com.eximia.exams.exception.ValidationException;
import com.eximia.exams.service.QuestionValidationStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MultipleChoiceValidationStrategy implements QuestionValidationStrategy {

    @Override
    public QuestionType getSupportedType() {
        return QuestionType.MULTIPLE_CHOICE;
    }

    @Override
    public void validate(Question question) {
        List<Option> options = question.getOptions();
        long correctCount = options.stream().filter(Option::getIsCorrect).count();
        if (correctCount != 1) {
            throw new ValidationException(String.format("Multiple choice question must have exactly one correct option, found %d", correctCount));
        }
        double sumPoints = options.stream().mapToDouble(Option::getPoints).sum();
        if (Double.compare(sumPoints, question.getPoints()) != 0) {
            throw new ValidationException(
                    String.format("Total points of options (%.2f) must match question points (%.2f)",
                            sumPoints, question.getPoints())
            );
        }
    }
}


package com.eximia.exams.service;

import com.eximia.exams.domain.entities.Option;
import com.eximia.exams.domain.entities.Question;
import com.eximia.exams.exception.ValidationException;

import java.util.List;

public class ValidationStrategyUtils {

    public static void validatePointsMatch(Question question) {
        List<Option> options = question.getOptions();

        double sumPoints = options.stream().mapToDouble(Option::getPoints).sum();
        if (Double.compare(sumPoints, question.getPoints()) != 0) {
            throw new ValidationException(
                    String.format("Total points of options (%.2f) must match question points (%.2f)",
                            sumPoints, question.getPoints())
            );
        }
    }

}

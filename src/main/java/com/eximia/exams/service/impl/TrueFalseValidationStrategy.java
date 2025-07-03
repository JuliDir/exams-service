package com.eximia.exams.service.impl;

import com.eximia.exams.domain.entities.Option;
import com.eximia.exams.domain.entities.Question;
import com.eximia.exams.domain.enums.QuestionType;
import com.eximia.exams.exception.ValidationException;
import com.eximia.exams.service.QuestionValidationStrategy;
import com.eximia.exams.service.ValidationStrategyUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TrueFalseValidationStrategy implements QuestionValidationStrategy {

    @Override
    public QuestionType getSupportedType() {
        return QuestionType.TRUE_FALSE;
    }

    @Override
    public void validate(Question question) {
        List<Option> options = question.getOptions();
        if (options.size() > 1) {
            throw new ValidationException("True or false question must have exactly one option, found " + options.size());
        }
        ValidationStrategyUtils.validatePointsMatch(question);
    }
}


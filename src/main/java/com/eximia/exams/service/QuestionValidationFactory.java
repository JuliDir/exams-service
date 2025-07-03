package com.eximia.exams.service;

import com.eximia.exams.domain.enums.QuestionType;
import com.eximia.exams.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class QuestionValidationFactory {
    private final Map<QuestionType, QuestionValidationStrategy> strategies;

    @Autowired
    public QuestionValidationFactory(List<QuestionValidationStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(QuestionValidationStrategy::getSupportedType, Function.identity()));
    }

    public QuestionValidationStrategy forType(QuestionType type) {
        QuestionValidationStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new ValidationException(String.format("No validation strategy found for question type: %s", type));
        }
        return strategy;
    }
}

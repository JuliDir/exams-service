package com.eximia.exams.service;

import com.eximia.exams.domain.entities.Question;
import com.eximia.exams.domain.enums.QuestionType;
import com.eximia.exams.exception.CustomException;

public interface QuestionValidationStrategy {
    void validate(Question question) throws CustomException;
    QuestionType getSupportedType();
}

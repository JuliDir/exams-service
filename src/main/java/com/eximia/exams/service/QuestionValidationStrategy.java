package com.eximia.exams.service;

import com.eximia.exams.domain.enums.QuestionType;
import com.eximia.exams.dto.request.QuestionRequestDto;
import com.eximia.exams.exception.CustomException;

public interface QuestionValidationStrategy {
    void validate(QuestionRequestDto questionRequestDto) throws CustomException;
    QuestionType getSupportedType();
}

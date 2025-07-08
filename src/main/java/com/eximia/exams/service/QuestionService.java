package com.eximia.exams.service;

import com.eximia.exams.dto.request.QuestionRequestDto;
import com.eximia.exams.dto.response.QuestionResponseDto;

import java.util.List;

public interface QuestionService {

    QuestionResponseDto createQuestion(String examId, QuestionRequestDto questionRequestDto);

    QuestionResponseDto getQuestionById(String id);

    List<QuestionResponseDto> getQuestionsByExamId(String examId);

    QuestionResponseDto updateQuestion(String id, QuestionRequestDto questionRequestDto);

    void deleteQuestion(String id);

    void deleteQuestionsByExamId(String examId);

}
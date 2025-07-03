package com.eximia.exams.service;

import com.eximia.exams.domain.enums.QuestionType;
import com.eximia.exams.dto.request.QuestionRequestDto;
import com.eximia.exams.dto.response.QuestionResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuestionService {

    QuestionResponseDto createQuestion(String examId, QuestionRequestDto questionRequestDto);

    QuestionResponseDto getQuestionById(String id);

    QuestionResponseDto getQuestionByIdAndExamId(String id, String examId);

    List<QuestionResponseDto> getQuestionsByExamId(String examId);

    Page<QuestionResponseDto> getQuestionsByExamId(String examId, Pageable pageable);

    List<QuestionResponseDto> getQuestionsByCreator(String createdBy);

    List<QuestionResponseDto> getQuestionsByType(QuestionType questionType);

    List<QuestionResponseDto> getQuestionsByExamIdAndType(String examId, QuestionType questionType);

    List<QuestionResponseDto> searchQuestionsByText(String questionText);

    List<QuestionResponseDto> getQuestionsByPointsRange(Double minPoints, Double maxPoints);

    QuestionResponseDto updateQuestion(String id, QuestionRequestDto questionRequestDto);

    QuestionResponseDto updateQuestionInExam(String id, String examId, QuestionRequestDto questionRequestDto);

    void deleteQuestion(String id);

    void deleteQuestionFromExam(String id, String examId);

    void deleteQuestionsByExamId(String examId);

    long countQuestionsByExamId(String examId);

    long countRequiredQuestionsByExamId(String examId);

}
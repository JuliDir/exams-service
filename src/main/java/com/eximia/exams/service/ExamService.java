package com.eximia.exams.service;

import com.eximia.exams.dto.request.ExamRequestDto;
import com.eximia.exams.dto.response.ExamResponseDto;

public interface ExamService {

    ExamResponseDto createExam(ExamRequestDto examRequestDto);

    ExamResponseDto getExamById(String id);

    ExamResponseDto updateExam(String id, ExamRequestDto examRequestDto);

    void deleteExam(String id);
}

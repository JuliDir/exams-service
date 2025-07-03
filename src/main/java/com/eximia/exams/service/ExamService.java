package com.eximia.exams.service;

import com.eximia.exams.dto.request.ExamRequestDto;
import com.eximia.exams.dto.response.ExamResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ExamService {

    ExamResponseDto createExam(ExamRequestDto examRequestDto);

    ExamResponseDto getExamById(String id);

    Page<ExamResponseDto> getAllExams(Pageable pageable);

    List<ExamResponseDto> getExamsByCreator(String createdBy);

    List<ExamResponseDto> getExamsBySubject(String subject);

    List<ExamResponseDto> searchExamsByTitle(String title);

    List<ExamResponseDto> getExamsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<ExamResponseDto> getExamsByPointsRange(Double minPoints, Double maxPoints);

    ExamResponseDto updateExam(String id, ExamRequestDto examRequestDto);

    void deleteExam(String id);
}

package com.eximia.exams.service.impl;

import com.eximia.exams.domain.entities.Exam;
import com.eximia.exams.dto.request.ExamRequestDto;
import com.eximia.exams.dto.request.QuestionRequestDto;
import com.eximia.exams.dto.response.ExamResponseDto;
import com.eximia.exams.dto.response.QuestionResponseDto;
import com.eximia.exams.exception.ExamNotFoundException;
import com.eximia.exams.exception.CustomException;
import com.eximia.exams.mapper.ExamMapper;
import com.eximia.exams.repository.ExamRepository;
import com.eximia.exams.service.ExamService;
import com.eximia.exams.service.PointsDistributionService;
import com.eximia.exams.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final ExamMapper examMapper;
    private final QuestionService questionService;
    private final PointsDistributionService pointsDistributionService;

    private static final double MAX_POINTS = 100.0;

    @Override
    @Transactional
    public ExamResponseDto createExam(ExamRequestDto examRequestDto) {
        log.info("Creating exam with title: {}", examRequestDto.getTitle());

        // Distribute question points
        pointsDistributionService.distributeExamPoints(examRequestDto);

        Exam exam = examMapper.toEntity(examRequestDto);
        exam.setQuestionIds(new ArrayList<>());
        exam.setTotalPoints(MAX_POINTS);

        Exam savedExam = examRepository.save(exam);

        List<String> questionIds = new ArrayList<>();

        for (QuestionRequestDto questionRequestDto : examRequestDto.getQuestions()) {
            QuestionResponseDto createdQuestion = questionService.createQuestion(savedExam.getId(), questionRequestDto);
            questionIds.add(createdQuestion.getId());
        }

        savedExam.setQuestionIds(questionIds);
        savedExam = examRepository.save(savedExam);

        log.info("Exam created successfully with ID: {}", savedExam.getId());

        ExamResponseDto responseDto = examMapper.toResponseDto(savedExam);
        responseDto.setQuestions(questionService.getQuestionsByExamId(savedExam.getId()));

        return responseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public ExamResponseDto getExamById(String id) {
        log.info("Fetching exam with ID: {}", id);

        Exam exam = findExamByIdOrThrow(id);
        ExamResponseDto responseDto = examMapper.toResponseDto(exam);

        responseDto.setQuestions(questionService.getQuestionsByExamId(id));

        return responseDto;
    }

    @Override
    @Transactional
    public ExamResponseDto updateExam(String id, ExamRequestDto examRequestDto) {
        log.info("Updating exam with ID: {}", id);

        Exam existingExam = findExamByIdOrThrow(id);

        examMapper.updateEntity(existingExam, examRequestDto);

        if (examRequestDto.getQuestions() != null && !examRequestDto.getQuestions().isEmpty()) {
            questionService.deleteQuestionsByExamId(id);

            List<String> questionIds = new ArrayList<>();
            double totalPoints = 0.0;

            for (var questionDto : examRequestDto.getQuestions()) {
                QuestionResponseDto createdQuestion = questionService.createQuestion(id, questionDto);
                questionIds.add(createdQuestion.getId());
                totalPoints += createdQuestion.getPoints();
            }

            existingExam.setQuestionIds(questionIds);
            existingExam.setTotalPoints(totalPoints);
        }

        Exam updatedExam = examRepository.save(existingExam);
        log.info("Exam updated successfully with ID: {}", updatedExam.getId());

        ExamResponseDto responseDto = examMapper.toResponseDto(updatedExam);
        responseDto.setQuestions(questionService.getQuestionsByExamId(updatedExam.getId()));

        return responseDto;
    }

    @Override
    @Transactional
    public void deleteExam(String id) {
        log.info("Deleting exam with ID: {}", id);

        if (!examRepository.existsById(id)) {
            throw new ExamNotFoundException("Exam not found with ID: " + id);
        }

        questionService.deleteQuestionsByExamId(id);

        examRepository.deleteById(id);
        log.info("Exam deleted successfully with ID: {}", id);
    }

    private Exam findExamByIdOrThrow(String id) {
        return examRepository.findById(id)
                .orElseThrow(() -> new ExamNotFoundException("Exam not found with ID: " + id));
    }

    private void validateExamDoesNotExist(String title, String createdBy) {
        if (examRepository.existsByTitleAndCreatedBy(title, createdBy)) {
            throw new CustomException(
                    String.format("Exam with title '%s' already exists for creator '%s'", title, createdBy)
            );
        }
    }
}
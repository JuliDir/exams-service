package com.eximia.exams.service.impl;

import com.eximia.exams.domain.entities.Exam;
import com.eximia.exams.dto.request.ExamRequestDto;
import com.eximia.exams.dto.request.QuestionRequestDto;
import com.eximia.exams.dto.response.ExamResponseDto;
import com.eximia.exams.dto.response.QuestionResponseDto;
import com.eximia.exams.exception.ExamNotFoundException;
import com.eximia.exams.exception.ValidationException;
import com.eximia.exams.mapper.ExamMapper;
import com.eximia.exams.repository.ExamRepository;
import com.eximia.exams.service.ExamService;
import com.eximia.exams.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final ExamMapper examMapper;
    private final QuestionService questionService;

    private static final double MAX_POINTS = 100.0;

    @Override
    @Transactional
    public ExamResponseDto createExam(ExamRequestDto examRequestDto) {
        log.info("Creating exam with title: {}", examRequestDto.getTitle());

        // Create exam entity first
        Exam exam = examMapper.createEntity(examRequestDto);
        exam.setQuestionIds(new ArrayList<>());
        exam.setTotalPoints(MAX_POINTS);

        Exam savedExam = examRepository.save(exam);

        // Create questions
        List<String> questionIds = new ArrayList<>();

        for (QuestionRequestDto questionRequestDto : examRequestDto.getQuestions()) {
            QuestionResponseDto createdQuestion = questionService.createQuestion(savedExam.getId(), questionRequestDto);
            questionIds.add(createdQuestion.getId());
        }

        // Update exam with question IDs
        savedExam.setQuestionIds(questionIds);
        savedExam = examRepository.save(savedExam);

        log.info("Exam created successfully with ID: {}", savedExam.getId());

        // Return response with questions loaded
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

        // Load questions
        responseDto.setQuestions(questionService.getQuestionsByExamId(id));

        return responseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExamResponseDto> getAllExams(Pageable pageable) {
        log.info("Fetching all exams with pagination: {}", pageable);

        Page<Exam> examPage = examRepository.findAll(pageable);
        return examPage.map(exam -> {
            ExamResponseDto dto = examMapper.toResponseDto(exam);
            dto.setQuestions(questionService.getQuestionsByExamId(exam.getId()));
            return dto;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponseDto> getExamsByCreator(String createdBy) {
        log.info("Fetching exams created by: {}", createdBy);

        List<Exam> exams = examRepository.findByCreatedBy(createdBy);
        return exams.stream()
                .map(exam -> {
                    ExamResponseDto dto = examMapper.toResponseDto(exam);
                    dto.setQuestions(questionService.getQuestionsByExamId(exam.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponseDto> getExamsBySubject(String subject) {
        log.info("Fetching exams by subject: {}", subject);

        List<Exam> exams = examRepository.findBySubject(subject);
        return exams.stream()
                .map(exam -> {
                    ExamResponseDto dto = examMapper.toResponseDto(exam);
                    dto.setQuestions(questionService.getQuestionsByExamId(exam.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponseDto> searchExamsByTitle(String title) {
        log.info("Searching exams by title: {}", title);

        List<Exam> exams = examRepository.findByTitleContainingIgnoreCase(title);
        return exams.stream()
                .map(exam -> {
                    ExamResponseDto dto = examMapper.toResponseDto(exam);
                    dto.setQuestions(questionService.getQuestionsByExamId(exam.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponseDto> getExamsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching exams between {} and {}", startDate, endDate);

        List<Exam> exams = examRepository.findByDateRange(startDate, endDate);
        return exams.stream()
                .map(exam -> {
                    ExamResponseDto dto = examMapper.toResponseDto(exam);
                    dto.setQuestions(questionService.getQuestionsByExamId(exam.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponseDto> getExamsByPointsRange(Double minPoints, Double maxPoints) {
        log.info("Fetching exams with points between {} and {}", minPoints, maxPoints);

        List<Exam> exams = examRepository.findByPointsRange(minPoints, maxPoints);
        return exams.stream()
                .map(exam -> {
                    ExamResponseDto dto = examMapper.toResponseDto(exam);
                    dto.setQuestions(questionService.getQuestionsByExamId(exam.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ExamResponseDto updateExam(String id, ExamRequestDto examRequestDto) {
        log.info("Updating exam with ID: {}", id);

        Exam existingExam = findExamByIdOrThrow(id);

        // Update exam basic fields
        examMapper.updateEntity(existingExam, examRequestDto);

        // If questions are provided, replace existing questions
        if (examRequestDto.getQuestions() != null && !examRequestDto.getQuestions().isEmpty()) {
            // Delete existing questions
            questionService.deleteQuestionsByExamId(id);

            // Create new questions
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

        // Return response with questions loaded
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

        // Delete associated questions (and their options)
        questionService.deleteQuestionsByExamId(id);

        // Delete the exam
        examRepository.deleteById(id);
        log.info("Exam deleted successfully with ID: {}", id);
    }

    private Exam findExamByIdOrThrow(String id) {
        return examRepository.findById(id)
                .orElseThrow(() -> new ExamNotFoundException("Exam not found with ID: " + id));
    }

    private void validateExamDoesNotExist(String title, String createdBy) {
        if (examRepository.existsByTitleAndCreatedBy(title, createdBy)) {
            throw new ValidationException(
                    String.format("Exam with title '%s' already exists for creator '%s'", title, createdBy)
            );
        }
    }
}
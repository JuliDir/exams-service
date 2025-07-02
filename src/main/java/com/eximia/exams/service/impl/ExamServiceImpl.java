package com.eximia.exams.service.impl;

import com.eximia.exams.domain.entities.Exam;
import com.eximia.exams.dto.request.ExamRequestDto;
import com.eximia.exams.dto.response.ExamResponseDto;
import com.eximia.exams.exception.ExamNotFoundException;
import com.eximia.exams.exception.ValidationException;
import com.eximia.exams.mapper.ExamMapper;
import com.eximia.exams.repository.ExamRepository;
import com.eximia.exams.service.ExamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final ExamMapper examMapper;

    @Override
    @Transactional
    public ExamResponseDto createExam(ExamRequestDto examRequestDto) {
        log.info("Creating exam with title: {}", examRequestDto.getTitle());

        validateExamDoesNotExist(examRequestDto.getTitle(), examRequestDto.getCreatedBy());

        Exam exam = examMapper.toEntity(examRequestDto);
        Exam savedExam = examRepository.save(exam);

        log.info("Exam created successfully with ID: {}", savedExam.getId());
        return examMapper.toResponseDto(savedExam);
    }

    @Override
    @Transactional(readOnly = true)
    public ExamResponseDto getExamById(String id) {
        log.info("Fetching exam with ID: {}", id);

        Exam exam = findExamByIdOrThrow(id);
        return examMapper.toResponseDto(exam);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExamResponseDto> getAllActiveExams(Pageable pageable) {
        log.info("Fetching active exams with pagination: {}", pageable);

        Page<Exam> examPage = examRepository.findByIsActiveTrue(pageable);
        return examPage.map(examMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponseDto> getExamsByCreator(String createdBy) {
        log.info("Fetching exams created by: {}", createdBy);

        List<Exam> exams = examRepository.findByCreatedBy(createdBy);
        return exams.stream()
                .map(examMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponseDto> getExamsByCategory(String category) {
        log.info("Fetching exams by category: {}", category);

        List<Exam> exams = examRepository.findByCategory(category);
        return exams.stream()
                .map(examMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponseDto> searchExamsByTitle(String title) {
        log.info("Searching exams by title: {}", title);

        List<Exam> exams = examRepository.findByTitleContainingIgnoreCase(title);
        return exams.stream()
                .map(examMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponseDto> getExamsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching exams between {} and {}", startDate, endDate);

        List<Exam> exams = examRepository.findActiveExamsByDateRange(startDate, endDate);
        return exams.stream()
                .map(examMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponseDto> getExamsByPointsRange(Double minPoints, Double maxPoints) {
        log.info("Fetching exams with points between {} and {}", minPoints, maxPoints);

        List<Exam> exams = examRepository.findByPointsRange(minPoints, maxPoints);
        return exams.stream()
                .map(examMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ExamResponseDto updateExam(String id, ExamRequestDto examRequestDto) {
        log.info("Updating exam with ID: {}", id);

        Exam existingExam = findExamByIdOrThrow(id);
        examMapper.updateEntity(existingExam, examRequestDto);

        Exam updatedExam = examRepository.save(existingExam);
        log.info("Exam updated successfully with ID: {}", updatedExam.getId());

        return examMapper.toResponseDto(updatedExam);
    }

    @Override
    @Transactional
    public void deactivateExam(String id) {
        log.info("Deactivating exam with ID: {}", id);

        Exam exam = findExamByIdOrThrow(id);
        exam.setIsActive(false);
        exam.setUpdatedAt(LocalDateTime.now());

        examRepository.save(exam);
        log.info("Exam deactivated successfully with ID: {}", id);
    }

    @Override
    @Transactional
    public void deleteExam(String id) {
        log.info("Deleting exam with ID: {}", id);

        if (!examRepository.existsById(id)) {
            throw new ExamNotFoundException("Exam not found with ID: " + id);
        }

        examRepository.deleteById(id);
        log.info("Exam deleted successfully with ID: {}", id);
    }

    private Exam findExamByIdOrThrow(String id) {
        return examRepository.findByIdAndIsActiveTrue(id)
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

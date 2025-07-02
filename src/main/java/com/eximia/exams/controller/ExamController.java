package com.eximia.exams.controller;

import com.eximia.exams.dto.request.ExamRequestDto;
import com.eximia.exams.dto.response.ExamResponseDto;
import com.eximia.exams.service.ExamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/exams")
@RequiredArgsConstructor
@Validated
public class ExamController {

    private final ExamService examService;

    @PostMapping
    public ResponseEntity<ExamResponseDto> createExam(@Valid @RequestBody ExamRequestDto examRequestDto) {
        log.info("REST: Creating exam with title: {}", examRequestDto.getTitle());

        ExamResponseDto responseDto = examService.createExam(examRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamResponseDto> getExamById(@PathVariable @NotBlank String id) {
        log.info("REST: Fetching exam with ID: {}", id);

        ExamResponseDto responseDto = examService.getExamById(id);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    public ResponseEntity<Page<ExamResponseDto>> getAllActiveExams(
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("REST: Fetching active exams with pagination");

        Page<ExamResponseDto> examPage = examService.getAllActiveExams(pageable);
        return ResponseEntity.ok(examPage);
    }

    @GetMapping("/creator/{createdBy}")
    public ResponseEntity<List<ExamResponseDto>> getExamsByCreator(
            @PathVariable @NotBlank String createdBy) {
        log.info("REST: Fetching exams by creator: {}", createdBy);

        List<ExamResponseDto> exams = examService.getExamsByCreator(createdBy);
        return ResponseEntity.ok(exams);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ExamResponseDto>> getExamsByCategory(
            @PathVariable @NotBlank String category) {
        log.info("REST: Fetching exams by category: {}", category);

        List<ExamResponseDto> exams = examService.getExamsByCategory(category);
        return ResponseEntity.ok(exams);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ExamResponseDto>> searchExamsByTitle(
            @RequestParam @NotBlank String title) {
        log.info("REST: Searching exams by title: {}", title);

        List<ExamResponseDto> exams = examService.searchExamsByTitle(title);
        return ResponseEntity.ok(exams);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<ExamResponseDto>> getExamsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("REST: Fetching exams by date range: {} to {}", startDate, endDate);

        List<ExamResponseDto> exams = examService.getExamsByDateRange(startDate, endDate);
        return ResponseEntity.ok(exams);
    }

    @GetMapping("/points-range")
    public ResponseEntity<List<ExamResponseDto>> getExamsByPointsRange(
            @RequestParam @PositiveOrZero Double minPoints,
            @RequestParam @PositiveOrZero Double maxPoints) {
        log.info("REST: Fetching exams by points range: {} to {}", minPoints, maxPoints);

        List<ExamResponseDto> exams = examService.getExamsByPointsRange(minPoints, maxPoints);
        return ResponseEntity.ok(exams);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExamResponseDto> updateExam(
            @PathVariable @NotBlank String id,
            @Valid @RequestBody ExamRequestDto examRequestDto) {
        log.info("REST: Updating exam with ID: {}", id);

        ExamResponseDto responseDto = examService.updateExam(id, examRequestDto);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateExam(@PathVariable @NotBlank String id) {
        log.info("REST: Deactivating exam with ID: {}", id);

        examService.deactivateExam(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable @NotBlank String id) {
        log.info("REST: Deleting exam with ID: {}", id);

        examService.deleteExam(id);
        return ResponseEntity.noContent().build();
    }
}

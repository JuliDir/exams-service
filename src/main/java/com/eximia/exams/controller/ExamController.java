package com.eximia.exams.controller;

import com.eximia.exams.dto.request.ExamRequestDto;
import com.eximia.exams.dto.response.ExamResponseDto;
import com.eximia.exams.service.ExamService;
import com.eximia.exams.dto.criteria.ExamCriteria;
import com.eximia.exams.service.ExamQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/exams")
@RequiredArgsConstructor
@Validated
@Tag(name = "Exam Management", description = "APIs for managing exams")
public class ExamController {

    private final ExamService examService;
    private final ExamQueryService examQueryService;

    @PostMapping
    @Operation(summary = "Create a new exam")
    public ResponseEntity<ExamResponseDto> createExam(@Valid @RequestBody ExamRequestDto examRequestDto) {
        log.info("REST: Creating exam with title: {}", examRequestDto.getTitle());
        ExamResponseDto responseDto = examService.createExam(examRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get exam by ID")
    public ResponseEntity<ExamResponseDto> getExamById(@PathVariable @NotBlank String id) {
        log.info("REST: Fetching exam with ID: {}", id);
        ExamResponseDto responseDto = examService.getExamById(id);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    @Operation(summary = "Get exams by criteria with pagination")
    public ResponseEntity<Page<ExamResponseDto>> getExamsByCriteria(
            @Parameter(description = "Search criteria") ExamCriteria criteria,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("REST: Fetching exams with criteria: {}", criteria);
        Page<ExamResponseDto> examPage = examQueryService.findByCriteria(criteria, pageable);
        return ResponseEntity.ok(examPage);
    }

    @GetMapping("/search")
    @Operation(summary = "Search exams by criteria without pagination")
    public ResponseEntity<List<ExamResponseDto>> searchExams(
            @Parameter(description = "Search criteria") ExamCriteria criteria) {
        log.info("REST: Searching exams with criteria: {}", criteria);
        List<ExamResponseDto> exams = examQueryService.findByCriteria(criteria);
        return ResponseEntity.ok(exams);
    }

    @GetMapping("/count")
    @Operation(summary = "Count exams by criteria")
    public ResponseEntity<Long> countExams(
            @Parameter(description = "Search criteria") ExamCriteria criteria) {
        log.info("REST: Counting exams with criteria: {}", criteria);
        long count = examQueryService.countByCriteria(criteria);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing exam")
    public ResponseEntity<ExamResponseDto> updateExam(
            @PathVariable @NotBlank String id,
            @Valid @RequestBody ExamRequestDto examRequestDto) {
        log.info("REST: Updating exam with ID: {}", id);
        ExamResponseDto responseDto = examService.updateExam(id, examRequestDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an exam")
    public ResponseEntity<Void> deleteExam(@PathVariable @NotBlank String id) {
        log.info("REST: Deleting exam with ID: {}", id);
        examService.deleteExam(id);
        return ResponseEntity.noContent().build();
    }
}
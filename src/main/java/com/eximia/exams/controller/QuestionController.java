package com.eximia.exams.controller;

import com.eximia.exams.dto.request.QuestionRequestDto;
import com.eximia.exams.dto.response.QuestionResponseDto;
import com.eximia.exams.service.QuestionService;
import com.eximia.exams.dto.criteria.QuestionCriteria;
import com.eximia.exams.service.QuestionQueryService;
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
@RequestMapping("/questions")
@RequiredArgsConstructor
@Validated
@Tag(name = "Question Management", description = "APIs for managing questions")
public class QuestionController {

    private final QuestionService questionService;
    private final QuestionQueryService questionQueryService;

    @PostMapping("/exam/{examId}")
    @Operation(summary = "Create a new question for an exam")
    public ResponseEntity<QuestionResponseDto> createQuestion(
            @PathVariable @NotBlank String examId,
            @Valid @RequestBody QuestionRequestDto questionRequestDto) {
        log.info("REST: Creating question for exam ID: {}", examId);
        QuestionResponseDto responseDto = questionService.createQuestion(examId, questionRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get question by ID")
    public ResponseEntity<QuestionResponseDto> getQuestionById(@PathVariable @NotBlank String id) {
        log.info("REST: Fetching question with ID: {}", id);
        QuestionResponseDto responseDto = questionService.getQuestionById(id);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    @Operation(summary = "Get questions by criteria with pagination")
    public ResponseEntity<Page<QuestionResponseDto>> getQuestionsByCriteria(
            @Parameter(description = "Search criteria") QuestionCriteria criteria,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("REST: Fetching questions with criteria: {}", criteria);
        Page<QuestionResponseDto> questionPage = questionQueryService.findByCriteria(criteria, pageable);
        return ResponseEntity.ok(questionPage);
    }

    @GetMapping("/search")
    @Operation(summary = "Search questions by criteria without pagination")
    public ResponseEntity<List<QuestionResponseDto>> searchQuestions(
            @Parameter(description = "Search criteria") QuestionCriteria criteria) {
        log.info("REST: Searching questions with criteria: {}", criteria);
        List<QuestionResponseDto> questions = questionQueryService.findByCriteria(criteria);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/count")
    @Operation(summary = "Count questions by criteria")
    public ResponseEntity<Long> countQuestions(
            @Parameter(description = "Search criteria") QuestionCriteria criteria) {
        log.info("REST: Counting questions with criteria: {}", criteria);
        long count = questionQueryService.countByCriteria(criteria);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing question")
    public ResponseEntity<QuestionResponseDto> updateQuestion(
            @PathVariable @NotBlank String id,
            @Valid @RequestBody QuestionRequestDto questionRequestDto) {
        log.info("REST: Updating question with ID: {}", id);
        QuestionResponseDto responseDto = questionService.updateQuestion(id, questionRequestDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a question")
    public ResponseEntity<Void> deleteQuestion(@PathVariable @NotBlank String id) {
        log.info("REST: Deleting question with ID: {}", id);
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/exam/{examId}")
    @Operation(summary = "Delete all questions for an exam")
    public ResponseEntity<Void> deleteQuestionsByExamId(@PathVariable @NotBlank String examId) {
        log.info("REST: Deleting all questions for exam ID: {}", examId);
        questionService.deleteQuestionsByExamId(examId);
        return ResponseEntity.noContent().build();
    }
}
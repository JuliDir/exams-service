package com.eximia.exams.controller;

import com.eximia.exams.domain.enums.QuestionType;
import com.eximia.exams.dto.request.QuestionRequestDto;
import com.eximia.exams.dto.response.QuestionResponseDto;
import com.eximia.exams.service.QuestionService;
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
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
@Validated
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/exam/{examId}")
    public ResponseEntity<QuestionResponseDto> createQuestion(
            @PathVariable @NotBlank String examId,
            @Valid @RequestBody QuestionRequestDto questionRequestDto) {
        log.info("REST: Creating question for exam ID: {}", examId);

        QuestionResponseDto responseDto = questionService.createQuestion(examId, questionRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponseDto> getQuestionById(@PathVariable @NotBlank String id) {
        log.info("REST: Fetching question with ID: {}", id);

        QuestionResponseDto responseDto = questionService.getQuestionById(id);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{id}/exam/{examId}")
    public ResponseEntity<QuestionResponseDto> getQuestionByIdAndExamId(
            @PathVariable @NotBlank String id,
            @PathVariable @NotBlank String examId) {
        log.info("REST: Fetching question with ID: {} for exam ID: {}", id, examId);

        QuestionResponseDto responseDto = questionService.getQuestionByIdAndExamId(id, examId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/exam/{examId}")
    public ResponseEntity<List<QuestionResponseDto>> getQuestionsByExamId(
            @PathVariable @NotBlank String examId) {
        log.info("REST: Fetching questions for exam ID: {}", examId);

        List<QuestionResponseDto> questions = questionService.getQuestionsByExamId(examId);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/exam/{examId}/paginated")
    public ResponseEntity<Page<QuestionResponseDto>> getQuestionsByExamIdPaginated(
            @PathVariable @NotBlank String examId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("REST: Fetching questions for exam ID: {} with pagination", examId);

        Page<QuestionResponseDto> questionPage = questionService.getQuestionsByExamId(examId, pageable);
        return ResponseEntity.ok(questionPage);
    }

    @GetMapping("/creator/{createdBy}")
    public ResponseEntity<List<QuestionResponseDto>> getQuestionsByCreator(
            @PathVariable @NotBlank String createdBy) {
        log.info("REST: Fetching questions by creator: {}", createdBy);

        List<QuestionResponseDto> questions = questionService.getQuestionsByCreator(createdBy);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/type/{questionType}")
    public ResponseEntity<List<QuestionResponseDto>> getQuestionsByType(
            @PathVariable QuestionType questionType) {
        log.info("REST: Fetching questions by type: {}", questionType);

        List<QuestionResponseDto> questions = questionService.getQuestionsByType(questionType);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/exam/{examId}/type/{questionType}")
    public ResponseEntity<List<QuestionResponseDto>> getQuestionsByExamIdAndType(
            @PathVariable @NotBlank String examId,
            @PathVariable QuestionType questionType) {
        log.info("REST: Fetching questions for exam ID: {} and type: {}", examId, questionType);

        List<QuestionResponseDto> questions = questionService.getQuestionsByExamIdAndType(examId, questionType);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/search")
    public ResponseEntity<List<QuestionResponseDto>> searchQuestionsByText(
            @RequestParam @NotBlank String text) {
        log.info("REST: Searching questions by text: {}", text);

        List<QuestionResponseDto> questions = questionService.searchQuestionsByText(text);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/points-range")
    public ResponseEntity<List<QuestionResponseDto>> getQuestionsByPointsRange(
            @RequestParam @PositiveOrZero Double minPoints,
            @RequestParam @PositiveOrZero Double maxPoints) {
        log.info("REST: Fetching questions by points range: {} to {}", minPoints, maxPoints);

        List<QuestionResponseDto> questions = questionService.getQuestionsByPointsRange(minPoints, maxPoints);
        return ResponseEntity.ok(questions);
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuestionResponseDto> updateQuestion(
            @PathVariable @NotBlank String id,
            @Valid @RequestBody QuestionRequestDto questionRequestDto) {
        log.info("REST: Updating question with ID: {}", id);

        QuestionResponseDto responseDto = questionService.updateQuestion(id, questionRequestDto);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{id}/exam/{examId}")
    public ResponseEntity<QuestionResponseDto> updateQuestionInExam(
            @PathVariable @NotBlank String id,
            @PathVariable @NotBlank String examId,
            @Valid @RequestBody QuestionRequestDto questionRequestDto) {
        log.info("REST: Updating question with ID: {} for exam ID: {}", id, examId);

        QuestionResponseDto responseDto = questionService.updateQuestionInExam(id, examId, questionRequestDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable @NotBlank String id) {
        log.info("REST: Deleting question with ID: {}", id);

        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/exam/{examId}")
    public ResponseEntity<Void> deleteQuestionFromExam(
            @PathVariable @NotBlank String id,
            @PathVariable @NotBlank String examId) {
        log.info("REST: Deleting question with ID: {} from exam ID: {}", id, examId);

        questionService.deleteQuestionFromExam(id, examId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/exam/{examId}")
    public ResponseEntity<Void> deleteQuestionsByExamId(@PathVariable @NotBlank String examId) {
        log.info("REST: Deleting all questions for exam ID: {}", examId);

        questionService.deleteQuestionsByExamId(examId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exam/{examId}/count")
    public ResponseEntity<Long> countQuestionsByExamId(@PathVariable @NotBlank String examId) {
        log.info("REST: Counting questions for exam ID: {}", examId);

        long count = questionService.countQuestionsByExamId(examId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/exam/{examId}/count/required")
    public ResponseEntity<Long> countRequiredQuestionsByExamId(@PathVariable @NotBlank String examId) {
        log.info("REST: Counting required questions for exam ID: {}", examId);

        long count = questionService.countRequiredQuestionsByExamId(examId);
        return ResponseEntity.ok(count);
    }
}
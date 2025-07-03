package com.eximia.exams.controller;

import com.eximia.exams.dto.request.OptionRequestDto;
import com.eximia.exams.dto.response.OptionResponseDto;
import com.eximia.exams.service.OptionService;
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
@RequestMapping("/options")
@RequiredArgsConstructor
@Validated
public class OptionController {

    private final OptionService optionService;

    @PostMapping("/question/{questionId}")
    public ResponseEntity<OptionResponseDto> createOption(
            @PathVariable @NotBlank String questionId,
            @Valid @RequestBody OptionRequestDto optionRequestDto) {
        log.info("REST: Creating option for question ID: {}", questionId);

        OptionResponseDto responseDto = optionService.createOption(questionId, optionRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OptionResponseDto> getOptionById(@PathVariable @NotBlank String id) {
        log.info("REST: Fetching option with ID: {}", id);

        OptionResponseDto responseDto = optionService.getOptionById(id);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{id}/question/{questionId}")
    public ResponseEntity<OptionResponseDto> getOptionByIdAndQuestionId(
            @PathVariable @NotBlank String id,
            @PathVariable @NotBlank String questionId) {
        log.info("REST: Fetching option with ID: {} for question ID: {}", id, questionId);

        OptionResponseDto responseDto = optionService.getOptionByIdAndQuestionId(id, questionId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/question/{questionId}")
    public ResponseEntity<List<OptionResponseDto>> getOptionsByQuestionId(
            @PathVariable @NotBlank String questionId) {
        log.info("REST: Fetching options for question ID: {}", questionId);

        List<OptionResponseDto> options = optionService.getOptionsByQuestionId(questionId);
        return ResponseEntity.ok(options);
    }

    @GetMapping("/question/{questionId}/paginated")
    public ResponseEntity<Page<OptionResponseDto>> getOptionsByQuestionIdPaginated(
            @PathVariable @NotBlank String questionId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("REST: Fetching options for question ID: {} with pagination", questionId);

        Page<OptionResponseDto> optionPage = optionService.getOptionsByQuestionId(questionId, pageable);
        return ResponseEntity.ok(optionPage);
    }

    @GetMapping("/creator/{createdBy}")
    public ResponseEntity<List<OptionResponseDto>> getOptionsByCreator(
            @PathVariable @NotBlank String createdBy) {
        log.info("REST: Fetching options by creator: {}", createdBy);

        List<OptionResponseDto> options = optionService.getOptionsByCreator(createdBy);
        return ResponseEntity.ok(options);
    }

    @GetMapping("/question/{questionId}/correct")
    public ResponseEntity<List<OptionResponseDto>> getCorrectOptionsByQuestionId(
            @PathVariable @NotBlank String questionId) {
        log.info("REST: Fetching correct options for question ID: {}", questionId);

        List<OptionResponseDto> options = optionService.getCorrectOptionsByQuestionId(questionId);
        return ResponseEntity.ok(options);
    }

    @GetMapping("/question/{questionId}/incorrect")
    public ResponseEntity<List<OptionResponseDto>> getIncorrectOptionsByQuestionId(
            @PathVariable @NotBlank String questionId) {
        log.info("REST: Fetching incorrect options for question ID: {}", questionId);

        List<OptionResponseDto> options = optionService.getIncorrectOptionsByQuestionId(questionId);
        return ResponseEntity.ok(options);
    }

    @GetMapping("/search")
    public ResponseEntity<List<OptionResponseDto>> searchOptionsByText(
            @RequestParam @NotBlank String text) {
        log.info("REST: Searching options by text: {}", text);

        List<OptionResponseDto> options = optionService.searchOptionsByText(text);
        return ResponseEntity.ok(options);
    }

    @GetMapping("/points-range")
    public ResponseEntity<List<OptionResponseDto>> getOptionsByPointsRange(
            @RequestParam @PositiveOrZero Double minPoints,
            @RequestParam @PositiveOrZero Double maxPoints) {
        log.info("REST: Fetching options by points range: {} to {}", minPoints, maxPoints);

        List<OptionResponseDto> options = optionService.getOptionsByPointsRange(minPoints, maxPoints);
        return ResponseEntity.ok(options);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OptionResponseDto> updateOption(
            @PathVariable @NotBlank String id,
            @Valid @RequestBody OptionRequestDto optionRequestDto) {
        log.info("REST: Updating option with ID: {}", id);

        OptionResponseDto responseDto = optionService.updateOption(id, optionRequestDto);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{id}/question/{questionId}")
    public ResponseEntity<OptionResponseDto> updateOptionInQuestion(
            @PathVariable @NotBlank String id,
            @PathVariable @NotBlank String questionId,
            @Valid @RequestBody OptionRequestDto optionRequestDto) {
        log.info("REST: Updating option with ID: {} for question ID: {}", id, questionId);

        OptionResponseDto responseDto = optionService.updateOptionInQuestion(id, questionId, optionRequestDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOption(@PathVariable @NotBlank String id) {
        log.info("REST: Deleting option with ID: {}", id);

        optionService.deleteOption(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/question/{questionId}")
    public ResponseEntity<Void> deleteOptionFromQuestion(
            @PathVariable @NotBlank String id,
            @PathVariable @NotBlank String questionId) {
        log.info("REST: Deleting option with ID: {} from question ID: {}", id, questionId);

        optionService.deleteOptionFromQuestion(id, questionId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/question/{questionId}")
    public ResponseEntity<Void> deleteOptionsByQuestionId(@PathVariable @NotBlank String questionId) {
        log.info("REST: Deleting all options for question ID: {}", questionId);

        optionService.deleteOptionsByQuestionId(questionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/question/{questionId}/count")
    public ResponseEntity<Long> countOptionsByQuestionId(@PathVariable @NotBlank String questionId) {
        log.info("REST: Counting options for question ID: {}", questionId);

        long count = optionService.countOptionsByQuestionId(questionId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/question/{questionId}/count/correct")
    public ResponseEntity<Long> countCorrectOptionsByQuestionId(@PathVariable @NotBlank String questionId) {
        log.info("REST: Counting correct options for question ID: {}", questionId);

        long count = optionService.countCorrectOptionsByQuestionId(questionId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/question/{questionId}/count/incorrect")
    public ResponseEntity<Long> countIncorrectOptionsByQuestionId(@PathVariable @NotBlank String questionId) {
        log.info("REST: Counting incorrect options for question ID: {}", questionId);

        long count = optionService.countIncorrectOptionsByQuestionId(questionId);
        return ResponseEntity.ok(count);
    }
}
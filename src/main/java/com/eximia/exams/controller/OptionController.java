package com.eximia.exams.controller;

import com.eximia.exams.dto.request.OptionRequestDto;
import com.eximia.exams.dto.response.OptionResponseDto;
import com.eximia.exams.service.OptionService;
import com.eximia.exams.dto.criteria.OptionCriteria;
import com.eximia.exams.service.OptionQueryService;
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
@RequestMapping("/options")
@RequiredArgsConstructor
@Validated
@Tag(name = "Option Management", description = "APIs for managing options")
public class OptionController {

    private final OptionService optionService;
    private final OptionQueryService optionQueryService;

    @PostMapping("/question/{questionId}")
    @Operation(summary = "Create a new option for a question")
    public ResponseEntity<OptionResponseDto> createOption(
            @PathVariable @NotBlank String questionId,
            @Valid @RequestBody OptionRequestDto optionRequestDto) {
        log.info("REST: Creating option for question ID: {}", questionId);
        OptionResponseDto responseDto = optionService.createOption(questionId, optionRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get option by ID")
    public ResponseEntity<OptionResponseDto> getOptionById(@PathVariable @NotBlank String id) {
        log.info("REST: Fetching option with ID: {}", id);
        OptionResponseDto responseDto = optionService.getOptionById(id);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    @Operation(summary = "Get options by criteria with pagination")
    public ResponseEntity<Page<OptionResponseDto>> getOptionsByCriteria(
            @Parameter(description = "Search criteria") OptionCriteria criteria,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("REST: Fetching options with criteria: {}", criteria);
        Page<OptionResponseDto> optionPage = optionQueryService.findByCriteria(criteria, pageable);
        return ResponseEntity.ok(optionPage);
    }

    @GetMapping("/search")
    @Operation(summary = "Search options by criteria without pagination")
    public ResponseEntity<List<OptionResponseDto>> searchOptions(
            @Parameter(description = "Search criteria") OptionCriteria criteria) {
        log.info("REST: Searching options with criteria: {}", criteria);
        List<OptionResponseDto> options = optionQueryService.findByCriteria(criteria);
        return ResponseEntity.ok(options);
    }

    @GetMapping("/count")
    @Operation(summary = "Count options by criteria")
    public ResponseEntity<Long> countOptions(
            @Parameter(description = "Search criteria") OptionCriteria criteria) {
        log.info("REST: Counting options with criteria: {}", criteria);
        long count = optionQueryService.countByCriteria(criteria);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing option")
    public ResponseEntity<OptionResponseDto> updateOption(
            @PathVariable @NotBlank String id,
            @Valid @RequestBody OptionRequestDto optionRequestDto) {
        log.info("REST: Updating option with ID: {}", id);
        OptionResponseDto responseDto = optionService.updateOption(id, optionRequestDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an option")
    public ResponseEntity<Void> deleteOption(@PathVariable @NotBlank String id) {
        log.info("REST: Deleting option with ID: {}", id);
        optionService.deleteOption(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/question/{questionId}")
    @Operation(summary = "Delete all options for a question")
    public ResponseEntity<Void> deleteOptionsByQuestionId(@PathVariable @NotBlank String questionId) {
        log.info("REST: Deleting all options for question ID: {}", questionId);
        optionService.deleteOptionsByQuestionId(questionId);
        return ResponseEntity.noContent().build();
    }
}
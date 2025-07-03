package com.eximia.exams.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamRequestDto {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Positive(message = "Duration must be positive")
    private Integer durationInMinutes;

    @Min(value = 0, message = "Passing score must be at least 0")
    @Max(value = 100, message = "Passing score must not exceed 100")
    @Builder.Default
    private Double passingScore = 60.0;

    @Valid
    @NotEmpty(message = "Exam must have at least one question")
    private List<QuestionRequestDto> questions;

    @Builder.Default
    private String createdBy = "system";

    private String subject;

    private String difficultyLevel;

    @Builder.Default
    private Boolean allowMultipleChoice = true;

    @Builder.Default
    private Boolean allowTrueFalse = true;
}

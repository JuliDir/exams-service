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

    @PositiveOrZero(message = "Total points must be positive or zero")
    private Double totalPoints;

    @PositiveOrZero(message = "Passing score must be positive or zero")
    private Double passingScore;

    @Valid
    @NotEmpty(message = "Exam must have at least one question")
    private List<QuestionRequestDto> questions;

    @NotBlank(message = "Created by is required")
    private String createdBy;

    private String category;

    private String difficultyLevel;
}

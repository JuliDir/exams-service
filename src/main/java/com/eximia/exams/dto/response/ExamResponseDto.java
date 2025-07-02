package com.eximia.exams.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamResponseDto {

    private String id;
    private String title;
    private String description;
    private Integer durationInMinutes;
    private Double totalPoints;
    private Double passingScore;
    private List<QuestionResponseDto> questions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private Boolean isActive;
    private String category;
    private String difficultyLevel;
}

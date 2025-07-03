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
    private Double passingScore;
    private List<QuestionResponseDto> questions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private String subject;
    private String difficultyLevel;
    private Boolean allowMultipleChoice;
    private Boolean allowTrueFalse;
    private Double totalPoints;
    private List<String> questionIds;
}

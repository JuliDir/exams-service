package com.eximia.exams.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionResponseDto {
    private String id;
    private String optionText;
    private Boolean isCorrect;
    private Integer orderIndex;
    private Double points;
    private String explanation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private String questionId;
}
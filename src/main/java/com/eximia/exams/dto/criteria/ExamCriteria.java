package com.eximia.exams.dto.criteria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamCriteria {

    private String id;
    private String title;
    private String description;
    private String subject;
    private String difficultyLevel;
    private String createdBy;
    private String updatedBy;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAtFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAtTo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime updatedAtFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime updatedAtTo;

    private Integer durationInMinutesMin;
    private Integer durationInMinutesMax;

    private Double passingScoreMin;
    private Double passingScoreMax;

    private Double totalPointsMin;
    private Double totalPointsMax;

    private Boolean allowMultipleChoice;
    private Boolean allowTrueFalse;

    private String searchText;
}
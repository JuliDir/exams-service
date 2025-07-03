package com.eximia.exams.dto.criteria;

import com.eximia.exams.domain.enums.QuestionType;
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
public class QuestionCriteria {

    private String id;
    private String examId;
    private String questionText;
    private QuestionType questionType;
    private String explanation;
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

    private Double pointsMin;
    private Double pointsMax;

    private Integer orderIndexMin;
    private Integer orderIndexMax;

    private String searchText;
}
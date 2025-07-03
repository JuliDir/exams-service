package com.eximia.exams.dto.response;

import com.eximia.exams.domain.enums.QuestionType;
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
public class QuestionResponseDto {
    private String id;
    private String questionText;
    private QuestionType questionType;
    private Double points;
    private String explanation;
    private List<OptionResponseDto> options;
    private Integer orderIndex;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private String examId;
    private List<String> optionIds;
}

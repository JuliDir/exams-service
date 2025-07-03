package com.eximia.exams.dto.response;

import com.eximia.exams.domain.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponseDto {
    private String questionId;
    private String questionText;
    private QuestionType questionType;
    private Double points;
    private List<OptionResponseDto> options;
    private Integer orderIndex;
}

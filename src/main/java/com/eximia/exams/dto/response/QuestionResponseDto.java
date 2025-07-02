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
    private Boolean isRequired;
    private String explanation;
    private List<OptionResponseDto> options;
    private String correctAnswer;
    private Integer timeLimitInSeconds;
    private Integer orderIndex;
}

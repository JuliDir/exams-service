package com.eximia.exams.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionResponseDto {

    private String optionId;
    private String optionText;
    private Boolean isCorrect;
    private String explanation;
    private Integer orderIndex;
    private String matchTarget;
    private String fillInAnswer;
}

package com.eximia.exams.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionRequestDto {

    @NotBlank(message = "Option text is required")
    @Size(max = 500, message = "Option text must not exceed 500 characters")
    private String optionText;

    @Builder.Default
    private Boolean isCorrect = false;

    private String explanation;

    private Integer orderIndex;

    private String matchTarget;

    private String fillInAnswer;
}

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

    @Size(max = 500, message = "Option text must not exceed 500 characters")
    private String optionText;

    @Builder.Default
    private Boolean isCorrect = false;

    @Min(value = 1, message = "Order index must be at least 1")
    private Integer orderIndex;

    @PositiveOrZero(message = "Points must be zero or positive")
    @Builder.Default
    private Double points = 0.0;

    @Size(max = 500, message = "Explanation must not exceed 500 characters")
    private String explanation;

}
package com.eximia.exams.dto.request;

import com.eximia.exams.domain.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRequestDto {

    @NotBlank(message = "Question text is required")
    @Size(max = 2000, message = "Question text must not exceed 2000 characters")
    private String questionText;

    @NotNull(message = "Question type is required")
    private QuestionType questionType;

    @PositiveOrZero(message = "Points must be positive or zero")
    private Double points;

    @Builder.Default
    private Boolean isRequired = true;

    @Size(max = 1000, message = "Explanation must not exceed 1000 characters")
    private String explanation;

    @Valid
    private List<OptionRequestDto> options;

    private String correctAnswer;
    private Integer timeLimitInSeconds;
    private Integer orderIndex;
}

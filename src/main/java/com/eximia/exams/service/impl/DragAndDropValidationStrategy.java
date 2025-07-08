package com.eximia.exams.service.impl;

import com.eximia.exams.domain.entities.Question;
import com.eximia.exams.domain.enums.QuestionType;
import com.eximia.exams.dto.response.OptionResponseDto;
import com.eximia.exams.exception.CustomException;
import com.eximia.exams.service.OptionService;
import com.eximia.exams.service.QuestionValidationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DragAndDropValidationStrategy implements QuestionValidationStrategy {

    private final OptionService optionService;

    @Override
    public QuestionType getSupportedType() {
        return QuestionType.DRAG_AND_DROP;
    }

    @Override
    public void validate(Question question) {
        List<OptionResponseDto> options = optionService.getOptionsByQuestionId(question.getId());

        if (options.size() < 2) {
            throw new CustomException(
                    "Drag and drop question must have at least two options, found " + options.size()
            );
        }

        // For drag and drop, all options should have orderIndex
        boolean hasOrderIndex = options.stream()
                .allMatch(option -> option.getOrderIndex() != null);

        if (!hasOrderIndex) {
            throw new CustomException(
                    "All options in drag and drop question must have order index"
            );
        }

        // Validate points match
        double sumPoints = options.stream()
                .mapToDouble(OptionResponseDto::getPoints)
                .sum();

        if (Double.compare(sumPoints, question.getPoints()) != 0) {
            throw new CustomException(
                    String.format("Total points of options (%.2f) must match question points (%.2f)",
                            sumPoints, question.getPoints())
            );
        }
    }
}
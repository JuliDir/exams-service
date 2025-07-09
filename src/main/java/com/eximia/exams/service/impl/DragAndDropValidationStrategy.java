package com.eximia.exams.service.impl;

import com.eximia.exams.domain.enums.QuestionType;
import com.eximia.exams.dto.request.OptionRequestDto;
import com.eximia.exams.dto.request.QuestionRequestDto;
import com.eximia.exams.exception.CustomException;
import com.eximia.exams.service.QuestionValidationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DragAndDropValidationStrategy implements QuestionValidationStrategy {

    @Override
    public QuestionType getSupportedType() {
        return QuestionType.DRAG_AND_DROP;
    }

    @Override
    public void validate(QuestionRequestDto questionRequestDto) {
        List<OptionRequestDto> options = questionRequestDto.getOptions();

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
    }
}
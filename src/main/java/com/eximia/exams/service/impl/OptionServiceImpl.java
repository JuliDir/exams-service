package com.eximia.exams.service.impl;

import com.eximia.exams.domain.entities.Option;
import com.eximia.exams.dto.request.OptionRequestDto;
import com.eximia.exams.dto.response.OptionResponseDto;
import com.eximia.exams.exception.ExamNotFoundException;
import com.eximia.exams.exception.ValidationException;
import com.eximia.exams.mapper.OptionMapper;
import com.eximia.exams.repository.OptionRepository;
import com.eximia.exams.repository.QuestionRepository;
import com.eximia.exams.service.OptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OptionServiceImpl implements OptionService {

    private final OptionRepository optionRepository;
    private final QuestionRepository questionRepository;
    private final OptionMapper optionMapper;

    @Override
    @Transactional(readOnly = true)
    public List<OptionResponseDto> getOptionsByQuestionId(String questionId) {
        log.info("Fetching options for question ID: {}", questionId);

        List<Option> options = optionRepository.findByQuestionIdOrderByOrderIndexAsc(questionId);
        return options.stream()
                .map(optionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OptionResponseDto> getOptionsByQuestionId(String questionId, Pageable pageable) {
        log.info("Fetching options for question ID: {} with pagination", questionId);

        Page<Option> optionPage = optionRepository.findByQuestionId(questionId, pageable);
        return optionPage.map(optionMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OptionResponseDto> getOptionsByCreator(String createdBy) {
        log.info("Fetching options created by: {}", createdBy);

        List<Option> options = optionRepository.findByCreatedBy(createdBy);
        return options.stream()
                .map(optionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OptionResponseDto> getCorrectOptionsByQuestionId(String questionId) {
        log.info("Fetching correct options for question ID: {}", questionId);

        List<Option> options = optionRepository.findCorrectOptionsByQuestionId(questionId);
        return options.stream()
                .map(optionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OptionResponseDto> getIncorrectOptionsByQuestionId(String questionId) {
        log.info("Fetching incorrect options for question ID: {}", questionId);

        List<Option> options = optionRepository.findIncorrectOptionsByQuestionId(questionId);
        return options.stream()
                .map(optionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OptionResponseDto> searchOptionsByText(String optionText) {
        log.info("Searching options by text: {}", optionText);

        List<Option> options = optionRepository.findByOptionTextContainingIgnoreCase(optionText);
        return options.stream()
                .map(optionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OptionResponseDto> getOptionsByPointsRange(Double minPoints, Double maxPoints) {
        log.info("Fetching options with points between {} and {}", minPoints, maxPoints);

        List<Option> options = optionRepository.findByPointsRange(minPoints, maxPoints);
        return options.stream()
                .map(optionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OptionResponseDto updateOption(String id, OptionRequestDto optionRequestDto) {
        log.info("Updating option with ID: {}", id);

        Option existingOption = findOptionByIdOrThrow(id);

        // Update option fields
        optionMapper.updateEntity(existingOption, optionRequestDto);
        existingOption.setUpdatedAt(LocalDateTime.now());

        Option updatedOption = optionRepository.save(existingOption);

        log.info("Option updated successfully with ID: {}", updatedOption.getId());
        return optionMapper.toResponseDto(updatedOption);
    }

    @Override
    @Transactional
    public OptionResponseDto updateOptionInQuestion(String id, String questionId, OptionRequestDto optionRequestDto) {
        log.info("Updating option with ID: {} for question ID: {}", id, questionId);

        Option existingOption = optionRepository.findByIdAndQuestionId(id, questionId)
                .orElseThrow(() -> new ExamNotFoundException("Option not found with ID: " + id + " for question ID: " + questionId));

        // Update option fields
        optionMapper.updateEntity(existingOption, optionRequestDto);
        existingOption.setUpdatedAt(LocalDateTime.now());

        Option updatedOption = optionRepository.save(existingOption);

        log.info("Option updated successfully with ID: {} for question ID: {}", updatedOption.getId(), questionId);
        return optionMapper.toResponseDto(updatedOption);
    }

    @Override
    @Transactional
    public void deleteOption(String id) {
        log.info("Deleting option with ID: {}", id);

        if (!optionRepository.existsById(id)) {
            throw new ExamNotFoundException("Option not found with ID: " + id);
        }

        optionRepository.deleteById(id);
        log.info("Option deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional
    public void deleteOptionFromQuestion(String id, String questionId) {
        log.info("Deleting option with ID: {} from question ID: {}", id, questionId);

        if (!optionRepository.existsByIdAndQuestionId(id, questionId)) {
            throw new ExamNotFoundException("Option not found with ID: " + id + " for question ID: " + questionId);
        }

        optionRepository.deleteById(id);
        log.info("Option deleted successfully with ID: {} from question ID: {}", id, questionId);
    }

    @Override
    @Transactional
    public void deleteOptionsByQuestionId(String questionId) {
        log.info("Deleting all options for question ID: {}", questionId);

        optionRepository.deleteByQuestionId(questionId);
        log.info("All options deleted successfully for question ID: {}", questionId);
    }

    @Override
    @Transactional
    public void deleteOptionsByQuestionIds(List<String> questionIds) {
        log.info("Deleting options for {} questions", questionIds.size());

        optionRepository.deleteByQuestionIdIn(questionIds);
        log.info("Options deleted successfully for {} questions", questionIds.size());
    }

    @Override
    @Transactional(readOnly = true)
    public long countOptionsByQuestionId(String questionId) {
        return optionRepository.countByQuestionId(questionId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countCorrectOptionsByQuestionId(String questionId) {
        return optionRepository.countCorrectOptionsByQuestionId(questionId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countIncorrectOptionsByQuestionId(String questionId) {
        return optionRepository.countIncorrectOptionsByQuestionId(questionId);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateOptionBelongsToQuestion(String optionId, String questionId) {
        if (!optionRepository.existsByIdAndQuestionId(optionId, questionId)) {
            throw new ValidationException("Option with ID: " + optionId + " does not belong to question ID: " + questionId);
        }
    }

    private Option findOptionByIdOrThrow(String id) {
        return optionRepository.findById(id)
                .orElseThrow(() -> new ExamNotFoundException("Option not found with ID: " + id));
    }

    public OptionResponseDto createOption(String questionId, OptionRequestDto optionRequestDto) {
        log.info("Creating option for question ID: {}", questionId);

        // Validate question exists
        if (!questionRepository.existsById(questionId)) {
            throw new ExamNotFoundException("Question not found with ID: " + questionId);
        }

        // Create option entity
        Option option = optionMapper.createEntity(optionRequestDto);
        option.setQuestionId(questionId);

        Option savedOption = optionRepository.save(option);

        log.info("Option created successfully with ID: {}", savedOption.getId());
        return optionMapper.toResponseDto(savedOption);
    }

    @Override
    @Transactional(readOnly = true)
    public OptionResponseDto getOptionById(String id) {
        log.info("Fetching option with ID: {}", id);

        Option option = findOptionByIdOrThrow(id);
        return optionMapper.toResponseDto(option);
    }

    @Override
    @Transactional(readOnly = true)
    public OptionResponseDto getOptionByIdAndQuestionId(String id, String questionId) {
        log.info("Fetching option with ID: {} for question ID: {}", id, questionId);

        Option option = optionRepository.findByIdAndQuestionId(id, questionId)
                .orElseThrow(() -> new ExamNotFoundException("Option not found with ID: " + id + " for question ID: " + questionId));

        return optionMapper.toResponseDto(option);
    }
}


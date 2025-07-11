package com.eximia.exams.service.impl;

import com.eximia.exams.domain.entities.Question;
import com.eximia.exams.dto.request.QuestionRequestDto;
import com.eximia.exams.dto.response.QuestionResponseDto;
import com.eximia.exams.exception.ExamNotFoundException;
import com.eximia.exams.mapper.QuestionMapper;
import com.eximia.exams.repository.ExamRepository;
import com.eximia.exams.repository.QuestionRepository;
import com.eximia.exams.service.OptionService;
import com.eximia.exams.service.PointsDistributionService;
import com.eximia.exams.service.QuestionService;
import com.eximia.exams.service.QuestionValidationFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final ExamRepository examRepository;
    private final QuestionMapper questionMapper;
    private final OptionService optionService;
    private final QuestionValidationFactory questionValidationFactory;
    private final PointsDistributionService pointsDistributionService;

    @Override
    @Transactional
    public QuestionResponseDto createQuestion(String examId, QuestionRequestDto questionRequestDto) {
        log.info("Creating question for exam ID: {}", examId);

        if (!examRepository.existsById(examId)) {
            throw new ExamNotFoundException("Exam not found with ID: " + examId);
        }

        // Validate and distribute points
        questionValidationFactory.forType(questionRequestDto.getQuestionType()).validate(questionRequestDto);
        pointsDistributionService.distributeQuestionPoints(questionRequestDto);

        Question question = questionMapper.toEntity(questionRequestDto);

        question.setExamId(examId);
        Question savedQuestion = questionRepository.save(question);
        String questionId = savedQuestion.getId();

        List<String> optionIds = questionRequestDto.getOptions().stream()
                .map(optionDto -> optionService.createOption(questionId, optionDto).getId())
                .collect(Collectors.toList());

        savedQuestion.setOptionIds(optionIds);
        savedQuestion = questionRepository.save(savedQuestion);

        log.info("Question created successfully with ID: {}", savedQuestion.getId());
        QuestionResponseDto questionResponseDto = questionMapper.toResponseDto(savedQuestion);
        questionResponseDto.setOptions(optionService.getOptionsByQuestionId(questionId));

        return questionResponseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionResponseDto getQuestionById(String id) {
        log.info("Fetching question with ID: {}", id);

        Question question = findQuestionByIdOrThrow(id);
        QuestionResponseDto questionResponseDto = questionMapper.toResponseDto(question);
        questionResponseDto.setOptions(optionService.getOptionsByQuestionId(id));

        return questionResponseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDto> getQuestionsByExamId(String examId) {
        log.info("Fetching questions for exam ID: {}", examId);

        List<Question> questions = questionRepository.findByExamIdOrderByOrderIndexAsc(examId);
        return questions.stream()
                .map(questionMapper::toResponseDto)
                .peek(questionResponseDto -> questionResponseDto.setOptions(optionService.getOptionsByQuestionId(questionResponseDto.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public QuestionResponseDto updateQuestion(String id, QuestionRequestDto questionRequestDto) {
        log.info("Updating question with ID: {}", id);

        questionValidationFactory.forType(questionRequestDto.getQuestionType()).validate(questionRequestDto);

        Question existingQuestion = findQuestionByIdOrThrow(id);
        questionMapper.updateEntity(existingQuestion, questionRequestDto);

        Question updatedQuestion = questionRepository.save(existingQuestion);

        log.info("Question updated successfully with ID: {}", updatedQuestion.getId());
        return questionMapper.toResponseDto(updatedQuestion);
    }

    @Override
    @Transactional
    public void deleteQuestion(String id) {
        log.info("Deleting question with ID: {}", id);

        findQuestionByIdOrThrow(id);

        optionService.deleteOptionsByQuestionId(id);

        questionRepository.deleteById(id);

        log.info("Question deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional
    public void deleteQuestionsByExamId(String examId) {
        log.info("Deleting all questions for exam ID: {}", examId);

        List<Question> questions = questionRepository.findByExamId(examId);
        List<String> questionIds = questions.stream()
                .map(Question::getId)
                .collect(Collectors.toList());

        optionService.deleteOptionsByQuestionIds(questionIds);

        questionRepository.deleteByExamId(examId);

        log.info("All questions deleted successfully for exam ID: {}", examId);
    }

    private Question findQuestionByIdOrThrow(String id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ExamNotFoundException("Question not found with ID: " + id));
    }
}
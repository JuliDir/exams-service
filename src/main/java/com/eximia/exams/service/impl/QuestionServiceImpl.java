package com.eximia.exams.service.impl;

import com.eximia.exams.domain.entities.Question;
import com.eximia.exams.domain.enums.QuestionType;
import com.eximia.exams.dto.request.QuestionRequestDto;
import com.eximia.exams.dto.response.QuestionResponseDto;
import com.eximia.exams.exception.ExamNotFoundException;
import com.eximia.exams.mapper.QuestionMapper;
import com.eximia.exams.repository.ExamRepository;
import com.eximia.exams.repository.QuestionRepository;
import com.eximia.exams.service.OptionService;
import com.eximia.exams.service.QuestionService;
import com.eximia.exams.service.QuestionValidationFactory;
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
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final ExamRepository examRepository;
    private final QuestionMapper questionMapper;
    private final OptionService optionService;
    private final QuestionValidationFactory questionValidationFactory;

    @Override
    @Transactional
    public QuestionResponseDto createQuestion(String examId, QuestionRequestDto questionRequestDto) {
        log.info("Creating question for exam ID: {}", examId);

        // Validate exam exists
        if (!examRepository.existsById(examId)) {
            throw new ExamNotFoundException("Exam not found with ID: " + examId);
        }

        // Create question entity
        Question question = questionMapper.createEntity(questionRequestDto);

        // Validate the complete question
        questionValidationFactory.forType(question.getQuestionType()).validate(question);

        // Save question
        question.setExamId(examId);
        Question savedQuestion = questionRepository.save(question);
        String questionId = savedQuestion.getId();

        // Create options for the question
        List<String> optionIds = questionRequestDto.getOptions().stream()
                .map(optionDto -> optionService.createOption(questionId, optionDto).getId())
                .collect(Collectors.toList());

        // Update question with option IDs
        savedQuestion.setOptionIds(optionIds);
        savedQuestion = questionRepository.save(savedQuestion);

        log.info("Question created successfully with ID: {}", savedQuestion.getId());
        return questionMapper.toResponseDto(savedQuestion);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionResponseDto getQuestionById(String id) {
        log.info("Fetching question with ID: {}", id);

        Question question = findQuestionByIdOrThrow(id);
        return questionMapper.toResponseDto(question);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionResponseDto getQuestionByIdAndExamId(String id, String examId) {
        log.info("Fetching question with ID: {} for exam ID: {}", id, examId);

        Question question = questionRepository.findByIdAndExamId(id, examId)
                .orElseThrow(() -> new ExamNotFoundException("Question not found with ID: " + id + " for exam ID: " + examId));

        return questionMapper.toResponseDto(question);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDto> getQuestionsByExamId(String examId) {
        log.info("Fetching questions for exam ID: {}", examId);

        List<Question> questions = questionRepository.findByExamIdOrderByOrderIndexAsc(examId);
        return questions.stream()
                .map(questionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponseDto> getQuestionsByExamId(String examId, Pageable pageable) {
        log.info("Fetching questions for exam ID: {} with pagination", examId);

        Page<Question> questionPage = questionRepository.findByExamId(examId, pageable);
        return questionPage.map(questionMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDto> getQuestionsByCreator(String createdBy) {
        log.info("Fetching questions created by: {}", createdBy);

        List<Question> questions = questionRepository.findByCreatedBy(createdBy);
        return questions.stream()
                .map(questionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDto> getQuestionsByType(QuestionType questionType) {
        log.info("Fetching questions by type: {}", questionType);

        List<Question> questions = questionRepository.findByQuestionType(questionType);
        return questions.stream()
                .map(questionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDto> getQuestionsByExamIdAndType(String examId, QuestionType questionType) {
        log.info("Fetching questions for exam ID: {} and type: {}", examId, questionType);

        List<Question> questions = questionRepository.findByExamIdAndQuestionType(examId, questionType);
        return questions.stream()
                .map(questionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDto> searchQuestionsByText(String questionText) {
        log.info("Searching questions by text: {}", questionText);

        List<Question> questions = questionRepository.findByQuestionTextContainingIgnoreCase(questionText);
        return questions.stream()
                .map(questionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDto> getQuestionsByPointsRange(Double minPoints, Double maxPoints) {
        log.info("Fetching questions with points between {} and {}", minPoints, maxPoints);

        List<Question> questions = questionRepository.findByPointsRange(minPoints, maxPoints);
        return questions.stream()
                .map(questionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public QuestionResponseDto updateQuestion(String id, QuestionRequestDto questionRequestDto) {
        log.info("Updating question with ID: {}", id);

        Question existingQuestion = findQuestionByIdOrThrow(id);

        // Update question fields
        questionMapper.updateEntity(existingQuestion, questionRequestDto);
        existingQuestion.setUpdatedAt(LocalDateTime.now());

        Question updatedQuestion = questionRepository.save(existingQuestion);

        // Validate the updated question
        questionValidationFactory.forType(updatedQuestion.getQuestionType()).validate(updatedQuestion);

        log.info("Question updated successfully with ID: {}", updatedQuestion.getId());
        return questionMapper.toResponseDto(updatedQuestion);
    }

    @Override
    @Transactional
    public QuestionResponseDto updateQuestionInExam(String id, String examId, QuestionRequestDto questionRequestDto) {
        log.info("Updating question with ID: {} for exam ID: {}", id, examId);

        Question existingQuestion = questionRepository.findByIdAndExamId(id, examId)
                .orElseThrow(() -> new ExamNotFoundException("Question not found with ID: " + id + " for exam ID: " + examId));

        // Update question fields
        questionMapper.updateEntity(existingQuestion, questionRequestDto);
        existingQuestion.setUpdatedAt(LocalDateTime.now());

        Question updatedQuestion = questionRepository.save(existingQuestion);

        // Validate the updated question
        questionValidationFactory.forType(updatedQuestion.getQuestionType()).validate(updatedQuestion);

        log.info("Question updated successfully with ID: {} for exam ID: {}", updatedQuestion.getId(), examId);
        return questionMapper.toResponseDto(updatedQuestion);
    }

    @Override
    @Transactional
    public void deleteQuestion(String id) {
        log.info("Deleting question with ID: {}", id);

        Question question = findQuestionByIdOrThrow(id);

        // Delete associated options first
        optionService.deleteOptionsByQuestionId(id);

        // Delete the question
        questionRepository.deleteById(id);

        log.info("Question deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional
    public void deleteQuestionFromExam(String id, String examId) {
        log.info("Deleting question with ID: {} from exam ID: {}", id, examId);

        if (!questionRepository.existsByIdAndExamId(id, examId)) {
            throw new ExamNotFoundException("Question not found with ID: " + id + " for exam ID: " + examId);
        }

        // Delete associated options first
        optionService.deleteOptionsByQuestionId(id);

        // Delete the question
        questionRepository.deleteById(id);

        log.info("Question deleted successfully with ID: {} from exam ID: {}", id, examId);
    }

    @Override
    @Transactional
    public void deleteQuestionsByExamId(String examId) {
        log.info("Deleting all questions for exam ID: {}", examId);

        List<Question> questions = questionRepository.findByExamId(examId);
        List<String> questionIds = questions.stream()
                .map(Question::getId)
                .collect(Collectors.toList());

        // Delete associated options first
        optionService.deleteOptionsByQuestionIds(questionIds);

        // Delete questions
        questionRepository.deleteByExamId(examId);

        log.info("All questions deleted successfully for exam ID: {}", examId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countQuestionsByExamId(String examId) {
        return questionRepository.countByExamId(examId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countRequiredQuestionsByExamId(String examId) {
        return questionRepository.countRequiredByExamId(examId);
    }

    private Question findQuestionByIdOrThrow(String id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ExamNotFoundException("Question not found with ID: " + id));
    }
}
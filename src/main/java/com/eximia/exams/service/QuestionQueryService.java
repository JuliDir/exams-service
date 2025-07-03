package com.eximia.exams.service;

import com.eximia.exams.domain.entities.Question;
import com.eximia.exams.dto.response.QuestionResponseDto;
import com.eximia.exams.mapper.QuestionMapper;
import com.eximia.exams.repository.QuestionRepository;
import com.eximia.exams.dto.criteria.QuestionCriteria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionQueryService {

    private final MongoTemplate mongoTemplate;
    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;
    private final OptionService optionService;

    public Page<QuestionResponseDto> findByCriteria(QuestionCriteria questionCriteria, Pageable pageable) {
        log.debug("Finding questions by criteria: {}", questionCriteria);

        Query query = createQuery(questionCriteria);
        query.with(pageable);

        List<Question> questions = mongoTemplate.find(query, Question.class);

        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Question.class);

        List<QuestionResponseDto> questionDtos = questions.stream()
                .map(question -> {
                    QuestionResponseDto dto = questionMapper.toResponseDto(question);
                    dto.setOptions(optionService.getOptionsByQuestionId(question.getId()));
                    return dto;
                })
                .collect(Collectors.toList());

        return PageableExecutionUtils.getPage(questionDtos, pageable, () -> total);
    }

    public List<QuestionResponseDto> findByCriteria(QuestionCriteria questionCriteria) {
        log.debug("Finding all questions by criteria: {}", questionCriteria);

        Query query = createQuery(questionCriteria);

        List<Question> questions = mongoTemplate.find(query, Question.class);

        return questions.stream()
                .map(question -> {
                    QuestionResponseDto dto = questionMapper.toResponseDto(question);
                    dto.setOptions(optionService.getOptionsByQuestionId(question.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public long countByCriteria(QuestionCriteria questionCriteria) {
        Query query = createQuery(questionCriteria);
        return mongoTemplate.count(query, Question.class);
    }

    private Query createQuery(QuestionCriteria questionCriteria) {
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        if (StringUtils.hasText(questionCriteria.getId())) {
            criteriaList.add(Criteria.where("id").is(questionCriteria.getId()));
        }
        if (StringUtils.hasText(questionCriteria.getExamId())) {
            criteriaList.add(Criteria.where("exam_id").is(questionCriteria.getExamId()));
        }
        if (StringUtils.hasText(questionCriteria.getQuestionText())) {
            Pattern pattern = Pattern.compile(questionCriteria.getQuestionText(), Pattern.CASE_INSENSITIVE);
            criteriaList.add(Criteria.where("question_text").regex(pattern));
        }
        if (questionCriteria.getQuestionType() != null) {
            criteriaList.add(Criteria.where("question_type").is(questionCriteria.getQuestionType()));
        }
        if (StringUtils.hasText(questionCriteria.getExplanation())) {
            Pattern pattern = Pattern.compile(questionCriteria.getExplanation(), Pattern.CASE_INSENSITIVE);
            criteriaList.add(Criteria.where("explanation").regex(pattern));
        }
        if (StringUtils.hasText(questionCriteria.getCreatedBy())) {
            criteriaList.add(Criteria.where("created_by").is(questionCriteria.getCreatedBy()));
        }
        if (StringUtils.hasText(questionCriteria.getUpdatedBy())) {
            criteriaList.add(Criteria.where("updated_by").is(questionCriteria.getUpdatedBy()));
        }
        if (questionCriteria.getCreatedAtFrom() != null || questionCriteria.getCreatedAtTo() != null) {
            Criteria dateCriteria = Criteria.where("created_at");
            if (questionCriteria.getCreatedAtFrom() != null) {
                dateCriteria = dateCriteria.gte(questionCriteria.getCreatedAtFrom());
            }
            if (questionCriteria.getCreatedAtTo() != null) {
                dateCriteria = dateCriteria.lte(questionCriteria.getCreatedAtTo());
            }
            criteriaList.add(dateCriteria);
        }
        if (questionCriteria.getUpdatedAtFrom() != null || questionCriteria.getUpdatedAtTo() != null) {
            Criteria dateCriteria = Criteria.where("updated_at");
            if (questionCriteria.getUpdatedAtFrom() != null) {
                dateCriteria = dateCriteria.gte(questionCriteria.getUpdatedAtFrom());
            }
            if (questionCriteria.getUpdatedAtTo() != null) {
                dateCriteria = dateCriteria.lte(questionCriteria.getUpdatedAtTo());
            }
            criteriaList.add(dateCriteria);
        }
        if (questionCriteria.getPointsMin() != null || questionCriteria.getPointsMax() != null) {
            Criteria pointsCriteria = Criteria.where("points");
            if (questionCriteria.getPointsMin() != null) {
                pointsCriteria = pointsCriteria.gte(questionCriteria.getPointsMin());
            }
            if (questionCriteria.getPointsMax() != null) {
                pointsCriteria = pointsCriteria.lte(questionCriteria.getPointsMax());
            }
            criteriaList.add(pointsCriteria);
        }
        if (questionCriteria.getOrderIndexMin() != null || questionCriteria.getOrderIndexMax() != null) {
            Criteria orderCriteria = Criteria.where("order_index");
            if (questionCriteria.getOrderIndexMin() != null) {
                orderCriteria = orderCriteria.gte(questionCriteria.getOrderIndexMin());
            }
            if (questionCriteria.getOrderIndexMax() != null) {
                orderCriteria = orderCriteria.lte(questionCriteria.getOrderIndexMax());
            }
            criteriaList.add(orderCriteria);
        }
        if (StringUtils.hasText(questionCriteria.getSearchText())) {
            Pattern pattern = Pattern.compile(questionCriteria.getSearchText(), Pattern.CASE_INSENSITIVE);
            criteriaList.add(new Criteria().orOperator(
                    Criteria.where("question_text").regex(pattern),
                    Criteria.where("explanation").regex(pattern)
            ));
        }
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        return query;
    }
}
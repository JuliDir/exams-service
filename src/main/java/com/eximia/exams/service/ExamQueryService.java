package com.eximia.exams.service;

import com.eximia.exams.domain.entities.Exam;
import com.eximia.exams.dto.response.ExamResponseDto;
import com.eximia.exams.mapper.ExamMapper;
import com.eximia.exams.repository.ExamRepository;
import com.eximia.exams.dto.criteria.ExamCriteria;
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
public class ExamQueryService {

    private final MongoTemplate mongoTemplate;
    private final ExamMapper examMapper;
    private final QuestionService questionService;

    public Page<ExamResponseDto> findByCriteria(ExamCriteria examCriteria, Pageable pageable) {
        log.debug("Finding exams by criteria: {}", examCriteria);

        Query query = createQuery(examCriteria);
        query.with(pageable);
        List<Exam> exams = mongoTemplate.find(query, Exam.class);
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Exam.class);
        List<ExamResponseDto> examDtos = exams.stream()
                .map(exam -> {
                    ExamResponseDto dto = examMapper.toResponseDto(exam);
                    dto.setQuestions(questionService.getQuestionsByExamId(exam.getId()));
                    return dto;
                })
                .collect(Collectors.toList());

        return PageableExecutionUtils.getPage(examDtos, pageable, () -> total);
    }

    public List<ExamResponseDto> findByCriteria(ExamCriteria examCriteria) {
        log.debug("Finding all exams by criteria: {}", examCriteria);

        Query query = createQuery(examCriteria);

        List<Exam> exams = mongoTemplate.find(query, Exam.class);

        return exams.stream()
                .map(exam -> {
                    ExamResponseDto dto = examMapper.toResponseDto(exam);
                    dto.setQuestions(questionService.getQuestionsByExamId(exam.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public long countByCriteria(ExamCriteria examCriteria) {
        Query query = createQuery(examCriteria);
        return mongoTemplate.count(query, Exam.class);
    }

    private Query createQuery(ExamCriteria examCriteria) {
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        if (StringUtils.hasText(examCriteria.getId())) {
            criteriaList.add(Criteria.where("id").is(examCriteria.getId()));
        }
        if (StringUtils.hasText(examCriteria.getTitle())) {
            Pattern pattern = Pattern.compile(examCriteria.getTitle(), Pattern.CASE_INSENSITIVE);
            criteriaList.add(Criteria.where("title").regex(pattern));
        }
        if (StringUtils.hasText(examCriteria.getDescription())) {
            Pattern pattern = Pattern.compile(examCriteria.getDescription(), Pattern.CASE_INSENSITIVE);
            criteriaList.add(Criteria.where("description").regex(pattern));
        }
        if (StringUtils.hasText(examCriteria.getSubject())) {
            criteriaList.add(Criteria.where("subject").is(examCriteria.getSubject()));
        }
        if (StringUtils.hasText(examCriteria.getDifficultyLevel())) {
            criteriaList.add(Criteria.where("difficulty_level").is(examCriteria.getDifficultyLevel()));
        }
        if (StringUtils.hasText(examCriteria.getCreatedBy())) {
            criteriaList.add(Criteria.where("created_by").is(examCriteria.getCreatedBy()));
        }
        if (StringUtils.hasText(examCriteria.getUpdatedBy())) {
            criteriaList.add(Criteria.where("updated_by").is(examCriteria.getUpdatedBy()));
        }
        if (examCriteria.getCreatedAtFrom() != null || examCriteria.getCreatedAtTo() != null) {
            Criteria dateCriteria = Criteria.where("created_at");
            if (examCriteria.getCreatedAtFrom() != null) {
                dateCriteria = dateCriteria.gte(examCriteria.getCreatedAtFrom());
            }
            if (examCriteria.getCreatedAtTo() != null) {
                dateCriteria = dateCriteria.lte(examCriteria.getCreatedAtTo());
            }
            criteriaList.add(dateCriteria);
        }
        if (examCriteria.getUpdatedAtFrom() != null || examCriteria.getUpdatedAtTo() != null) {
            Criteria dateCriteria = Criteria.where("updated_at");
            if (examCriteria.getUpdatedAtFrom() != null) {
                dateCriteria = dateCriteria.gte(examCriteria.getUpdatedAtFrom());
            }
            if (examCriteria.getUpdatedAtTo() != null) {
                dateCriteria = dateCriteria.lte(examCriteria.getUpdatedAtTo());
            }
            criteriaList.add(dateCriteria);
        }
        if (examCriteria.getDurationInMinutesMin() != null || examCriteria.getDurationInMinutesMax() != null) {
            Criteria durationCriteria = Criteria.where("duration_minutes");
            if (examCriteria.getDurationInMinutesMin() != null) {
                durationCriteria = durationCriteria.gte(examCriteria.getDurationInMinutesMin());
            }
            if (examCriteria.getDurationInMinutesMax() != null) {
                durationCriteria = durationCriteria.lte(examCriteria.getDurationInMinutesMax());
            }
            criteriaList.add(durationCriteria);
        }
        if (examCriteria.getPassingScoreMin() != null || examCriteria.getPassingScoreMax() != null) {
            Criteria scoreCriteria = Criteria.where("passing_score");
            if (examCriteria.getPassingScoreMin() != null) {
                scoreCriteria = scoreCriteria.gte(examCriteria.getPassingScoreMin());
            }
            if (examCriteria.getPassingScoreMax() != null) {
                scoreCriteria = scoreCriteria.lte(examCriteria.getPassingScoreMax());
            }
            criteriaList.add(scoreCriteria);
        }
        if (examCriteria.getTotalPointsMin() != null || examCriteria.getTotalPointsMax() != null) {
            Criteria pointsCriteria = Criteria.where("total_points");
            if (examCriteria.getTotalPointsMin() != null) {
                pointsCriteria = pointsCriteria.gte(examCriteria.getTotalPointsMin());
            }
            if (examCriteria.getTotalPointsMax() != null) {
                pointsCriteria = pointsCriteria.lte(examCriteria.getTotalPointsMax());
            }
            criteriaList.add(pointsCriteria);
        }
        if (examCriteria.getAllowMultipleChoice() != null) {
            criteriaList.add(Criteria.where("allow_multiple_choice").is(examCriteria.getAllowMultipleChoice()));
        }
        if (examCriteria.getAllowTrueFalse() != null) {
            criteriaList.add(Criteria.where("allow_true_false").is(examCriteria.getAllowTrueFalse()));
        }
        if (StringUtils.hasText(examCriteria.getSearchText())) {
            Pattern pattern = Pattern.compile(examCriteria.getSearchText(), Pattern.CASE_INSENSITIVE);
            criteriaList.add(new Criteria().orOperator(
                    Criteria.where("title").regex(pattern),
                    Criteria.where("description").regex(pattern),
                    Criteria.where("subject").regex(pattern)
            ));
        }
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        return query;
    }
}
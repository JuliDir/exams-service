package com.eximia.exams.service;

import com.eximia.exams.domain.entities.Option;
import com.eximia.exams.dto.response.OptionResponseDto;
import com.eximia.exams.mapper.OptionMapper;
import com.eximia.exams.repository.OptionRepository;
import com.eximia.exams.dto.criteria.OptionCriteria;
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
public class OptionQueryService {

    private final MongoTemplate mongoTemplate;
    private final OptionRepository optionRepository;
    private final OptionMapper optionMapper;

    public Page<OptionResponseDto> findByCriteria(OptionCriteria optionCriteria, Pageable pageable) {
        log.debug("Finding options by criteria: {}", optionCriteria);

        Query query = createQuery(optionCriteria);
        query.with(pageable);

        List<Option> options = mongoTemplate.find(query, Option.class);

        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Option.class);

        List<OptionResponseDto> optionDtos = options.stream()
                .map(optionMapper::toResponseDto)
                .collect(Collectors.toList());

        return PageableExecutionUtils.getPage(optionDtos, pageable, () -> total);
    }

    public List<OptionResponseDto> findByCriteria(OptionCriteria optionCriteria) {
        log.debug("Finding all options by criteria: {}", optionCriteria);

        Query query = createQuery(optionCriteria);

        List<Option> options = mongoTemplate.find(query, Option.class);

        return options.stream()
                .map(optionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public long countByCriteria(OptionCriteria optionCriteria) {
        Query query = createQuery(optionCriteria);
        return mongoTemplate.count(query, Option.class);
    }

    private Query createQuery(OptionCriteria optionCriteria) {
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        if (StringUtils.hasText(optionCriteria.getId())) {
            criteriaList.add(Criteria.where("id").is(optionCriteria.getId()));
        }
        if (StringUtils.hasText(optionCriteria.getQuestionId())) {
            criteriaList.add(Criteria.where("question_id").is(optionCriteria.getQuestionId()));
        }
        if (StringUtils.hasText(optionCriteria.getOptionText())) {
            Pattern pattern = Pattern.compile(optionCriteria.getOptionText(), Pattern.CASE_INSENSITIVE);
            criteriaList.add(Criteria.where("option_text").regex(pattern));
        }
        if (optionCriteria.getIsCorrect() != null) {
            criteriaList.add(Criteria.where("is_correct").is(optionCriteria.getIsCorrect()));
        }
        if (StringUtils.hasText(optionCriteria.getExplanation())) {
            Pattern pattern = Pattern.compile(optionCriteria.getExplanation(), Pattern.CASE_INSENSITIVE);
            criteriaList.add(Criteria.where("explanation").regex(pattern));
        }
        if (StringUtils.hasText(optionCriteria.getCreatedBy())) {
            criteriaList.add(Criteria.where("created_by").is(optionCriteria.getCreatedBy()));
        }
        if (StringUtils.hasText(optionCriteria.getUpdatedBy())) {
            criteriaList.add(Criteria.where("updated_by").is(optionCriteria.getUpdatedBy()));
        }
        if (optionCriteria.getCreatedAtFrom() != null || optionCriteria.getCreatedAtTo() != null) {
            Criteria dateCriteria = Criteria.where("created_at");
            if (optionCriteria.getCreatedAtFrom() != null) {
                dateCriteria = dateCriteria.gte(optionCriteria.getCreatedAtFrom());
            }
            if (optionCriteria.getCreatedAtTo() != null) {
                dateCriteria = dateCriteria.lte(optionCriteria.getCreatedAtTo());
            }
            criteriaList.add(dateCriteria);
        }
        if (optionCriteria.getUpdatedAtFrom() != null || optionCriteria.getUpdatedAtTo() != null) {
            Criteria dateCriteria = Criteria.where("updated_at");
            if (optionCriteria.getUpdatedAtFrom() != null) {
                dateCriteria = dateCriteria.gte(optionCriteria.getUpdatedAtFrom());
            }
            if (optionCriteria.getUpdatedAtTo() != null) {
                dateCriteria = dateCriteria.lte(optionCriteria.getUpdatedAtTo());
            }
            criteriaList.add(dateCriteria);
        }
        if (optionCriteria.getPointsMin() != null || optionCriteria.getPointsMax() != null) {
            Criteria pointsCriteria = Criteria.where("points");
            if (optionCriteria.getPointsMin() != null) {
                pointsCriteria = pointsCriteria.gte(optionCriteria.getPointsMin());
            }
            if (optionCriteria.getPointsMax() != null) {
                pointsCriteria = pointsCriteria.lte(optionCriteria.getPointsMax());
            }
            criteriaList.add(pointsCriteria);
        }
        if (optionCriteria.getOrderIndexMin() != null || optionCriteria.getOrderIndexMax() != null) {
            Criteria orderCriteria = Criteria.where("order_index");
            if (optionCriteria.getOrderIndexMin() != null) {
                orderCriteria = orderCriteria.gte(optionCriteria.getOrderIndexMin());
            }
            if (optionCriteria.getOrderIndexMax() != null) {
                orderCriteria = orderCriteria.lte(optionCriteria.getOrderIndexMax());
            }
            criteriaList.add(orderCriteria);
        }
        if (StringUtils.hasText(optionCriteria.getSearchText())) {
            Pattern pattern = Pattern.compile(optionCriteria.getSearchText(), Pattern.CASE_INSENSITIVE);
            criteriaList.add(new Criteria().orOperator(
                    Criteria.where("option_text").regex(pattern),
                    Criteria.where("explanation").regex(pattern)
            ));
        }
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        return query;
    }
}
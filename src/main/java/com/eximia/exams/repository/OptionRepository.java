package com.eximia.exams.repository;

import com.eximia.exams.domain.entities.Option;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OptionRepository extends MongoRepository<Option, String> {

    List<Option> findByQuestionId(String questionId);

    List<Option> findByQuestionIdOrderByOrderIndexAsc(String questionId);

    List<Option> findByCreatedBy(String createdBy);

    @Query("{ 'questionId': ?0, 'isCorrect': true }")
    List<Option> findCorrectOptionsByQuestionId(String questionId);

    @Query("{ 'questionId': ?0, 'isCorrect': false }")
    List<Option> findIncorrectOptionsByQuestionId(String questionId);

    @Query("{ 'optionText': { $regex: ?0, $options: 'i' } }")
    List<Option> findByOptionTextContainingIgnoreCase(String optionText);

    @Query("{ 'points': { $gte: ?0, $lte: ?1 } }")
    List<Option> findByPointsRange(Double minPoints, Double maxPoints);

    Page<Option> findByQuestionId(String questionId, Pageable pageable);

    Optional<Option> findByIdAndQuestionId(String id, String questionId);

    boolean existsByIdAndQuestionId(String id, String questionId);

    void deleteByQuestionId(String questionId);

    @Query("{ 'questionId': { $in: ?0 } }")
    void deleteByQuestionIdIn(List<String> questionIds);

    @Query("{ 'questionId': ?0 }")
    long countByQuestionId(String questionId);

    @Query("{ 'questionId': ?0, 'isCorrect': true }")
    long countCorrectOptionsByQuestionId(String questionId);

    @Query("{ 'questionId': ?0, 'isCorrect': false }")
    long countIncorrectOptionsByQuestionId(String questionId);
}
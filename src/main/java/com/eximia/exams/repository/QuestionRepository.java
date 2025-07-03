package com.eximia.exams.repository;

import com.eximia.exams.domain.entities.Question;
import com.eximia.exams.domain.enums.QuestionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends MongoRepository<Question, String> {

    List<Question> findByExamId(String examId);

    List<Question> findByExamIdOrderByOrderIndexAsc(String examId);

    List<Question> findByCreatedBy(String createdBy);

    List<Question> findByQuestionType(QuestionType questionType);

    @Query("{ 'examId': ?0, 'questionType': ?1 }")
    List<Question> findByExamIdAndQuestionType(String examId, QuestionType questionType);

    @Query("{ 'questionText': { $regex: ?0, $options: 'i' } }")
    List<Question> findByQuestionTextContainingIgnoreCase(String questionText);

    @Query("{ 'points': { $gte: ?0, $lte: ?1 } }")
    List<Question> findByPointsRange(Double minPoints, Double maxPoints);

    Page<Question> findByExamId(String examId, Pageable pageable);

    Optional<Question> findByIdAndExamId(String id, String examId);

    boolean existsByIdAndExamId(String id, String examId);

    void deleteByExamId(String examId);

    @Query("{ 'examId': ?0 }")
    long countByExamId(String examId);

    @Query("{ 'examId': ?0, 'isRequired': true }")
    long countRequiredByExamId(String examId);
}
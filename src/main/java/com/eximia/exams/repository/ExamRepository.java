package com.eximia.exams.repository;

import com.eximia.exams.domain.entities.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamRepository extends MongoRepository<Exam, String> {

    Page<Exam> findByIsActiveTrue(Pageable pageable);

    List<Exam> findByCreatedBy(String createdBy);

    List<Exam> findByCategory(String category);

    List<Exam> findByDifficultyLevel(String difficultyLevel);

    @Query("{ 'title': { $regex: ?0, $options: 'i' } }")
    List<Exam> findByTitleContainingIgnoreCase(String title);

    @Query("{ 'isActive': true, 'createdAt': { $gte: ?0, $lte: ?1 } }")
    List<Exam> findActiveExamsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    @Query("{ 'totalPoints': { $gte: ?0, $lte: ?1 } }")
    List<Exam> findByPointsRange(Double minPoints, Double maxPoints);

    Optional<Exam> findByIdAndIsActiveTrue(String id);

    boolean existsByTitleAndCreatedBy(String title, String createdBy);
}
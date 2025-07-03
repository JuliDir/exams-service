package com.eximia.exams.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "exams")
public class Exam {

    @Id
    private String id;

    @Field("title")
    private String title;

    @Field("description")
    private String description;

    @Field("duration_minutes")
    private Integer durationInMinutes;

    @Field("passing_score")
    private Double passingScore;

    @Field("questions")
    private List<Question> questions;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    @Field("created_by")
    private String createdBy;

    @Field("subject")
    private String subject;

    @Field("difficulty_level")
    private String difficultyLevel;

    @Field("allow_multiple_choice")
    private Boolean allowMultipleChoice;
}

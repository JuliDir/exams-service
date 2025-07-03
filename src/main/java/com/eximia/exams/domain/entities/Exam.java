package com.eximia.exams.domain.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "exams")
public class Exam extends AuditableEntity {

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

    @Field("question_ids")
    private List<String> questionIds;

    @Field("subject")
    private String subject;

    @Field("difficulty_level")
    private String difficultyLevel;

    @Field("allow_multiple_choice")
    private Boolean allowMultipleChoice;

    @Field("allow_true_false")
    private Boolean allowTrueFalse;

    @Field("total_points")
    private Double totalPoints;
}
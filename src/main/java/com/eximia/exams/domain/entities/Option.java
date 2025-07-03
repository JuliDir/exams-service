package com.eximia.exams.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "options")
public class Option extends AuditableEntity {

    @Id
    private String id;

    @Field("option_text")
    private String optionText;

    @Field("is_correct")
    private Boolean isCorrect;

    @Field("points")
    private Double points;

    @Field("order_index")
    private Integer orderIndex;

    @Field("explanation")
    private String explanation;

    @Field("question_id")
    private String questionId;
}
package com.eximia.exams.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import com.eximia.exams.domain.enums.QuestionType;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "questions")
public class Question extends AuditableEntity {

    @Id
    private String id;

    @Field("question_text")
    private String questionText;

    @Field("question_type")
    private QuestionType questionType;

    @Field("points")
    private Double points;

    @Field("option_ids")
    private List<String> optionIds;

    @Field("order_index")
    private Integer orderIndex;

    @Field("explanation")
    private String explanation;

    @Field("exam_id")
    private String examId;
}
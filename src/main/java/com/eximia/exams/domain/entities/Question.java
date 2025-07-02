package com.eximia.exams.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;
import com.eximia.exams.domain.enums.QuestionType;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Field("question_id")
    private String questionId;

    @Field("question_text")
    private String questionText;

    @Field("question_type")
    private QuestionType questionType;

    @Field("points")
    private Double points;

    @Field("is_required")
    private Boolean isRequired;

    @Field("explanation")
    private String explanation;

    @Field("options")
    private List<Option> options;

    @Field("correct_answer")
    private String correctAnswer;

    @Field("time_limit_seconds")
    private Integer timeLimitInSeconds;

    @Field("order_index")
    private Integer orderIndex;
}

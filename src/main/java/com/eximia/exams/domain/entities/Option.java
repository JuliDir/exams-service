package com.eximia.exams.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Option {

    @Field("option_id")
    private String optionId;

    @Field("option_text")
    private String optionText;

    @Field("is_correct")
    private Boolean isCorrect;

    @Field("points")
    private Double points;

    @Field("order_index")
    private Integer orderIndex;
}

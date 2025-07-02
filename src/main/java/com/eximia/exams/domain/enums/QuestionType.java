package com.eximia.exams.domain.enums;

public enum QuestionType {
    OPEN_ENDED("open_ended"),
    MULTIPLE_CHOICE("multiple_choice"),
    MULTIPLE_SELECTION("multiple_selection"),
    MATCHING("matching"),
    TRUE_FALSE("true_false"),
    FILL_IN_THE_BLANK("fill_in_the_blank"),
    DRAG_AND_DROP("drag_and_drop");

    private final String value;

    QuestionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static QuestionType fromValue(String value) {
        for (QuestionType type : QuestionType.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown question type: " + value);
    }
}

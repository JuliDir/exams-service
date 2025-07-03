package com.eximia.exams.domain.enums;

public enum QuestionType {
    MULTIPLE_CHOICE("MULTIPLE_CHOICE"),
    MULTIPLE_SELECTION("MULTIPLE_SELECTION"),
    TRUE_FALSE("TRUE_FALSE"),
    DRAG_AND_DROP("DRAG_AND_DROP"),;

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

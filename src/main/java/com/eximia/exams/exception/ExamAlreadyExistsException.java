package com.eximia.exams.exception;

public class ExamAlreadyExistsException extends RuntimeException {
    public ExamAlreadyExistsException(String message) {
        super(message);
    }
}

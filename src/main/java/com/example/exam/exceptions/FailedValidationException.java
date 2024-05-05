package com.example.exam.exceptions;

public class FailedValidationException extends RuntimeException {
    public FailedValidationException() {
        super();
    }

    public FailedValidationException(String message) {
        super(message);
    }
}

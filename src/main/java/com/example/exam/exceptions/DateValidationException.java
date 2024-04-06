package com.example.exam.exceptions;

public class DateValidationException extends RuntimeException {
    public DateValidationException() {
    }

    public DateValidationException(String message) {
        super(message);
    }
}

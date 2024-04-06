package com.example.exam.exceptions;

public class EmailValidationException extends RuntimeException {
    public EmailValidationException() {
        super();
    }

    public EmailValidationException(String message) {
        super(message);
    }
}

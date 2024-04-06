package com.example.exam.exceptions;

public class PeselValidationException extends RuntimeException {
    public PeselValidationException() {
    }

    public PeselValidationException(String message) {
        super(message);
    }
}

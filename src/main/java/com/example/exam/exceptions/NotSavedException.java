package com.example.exam.exceptions;

public class NotSavedException extends RuntimeException {
    public NotSavedException() {
    }

    public NotSavedException(String message) {
        super(message);
    }
}

package com.example.exam.exceptions;

public class NoCurrentPositionException extends RuntimeException {
    public NoCurrentPositionException() {
    }

    public NoCurrentPositionException(String message) {
        super(message);
    }
}

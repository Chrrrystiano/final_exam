package com.example.exam.exceptions;

public class UnrecognizedTypeException extends RuntimeException{
    public UnrecognizedTypeException() {
    }

    public UnrecognizedTypeException(String message) {
        super(message);
    }
}

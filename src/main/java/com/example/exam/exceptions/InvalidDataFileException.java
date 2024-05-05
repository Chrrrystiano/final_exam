package com.example.exam.exceptions;

public class InvalidDataFileException extends RuntimeException {
    public InvalidDataFileException() {
    }

    public InvalidDataFileException(String message) {
        super(message);
    }
}

package com.example.exam.exceptions;

public class StatusNotFoundException extends RuntimeException{
    public StatusNotFoundException() {
    }

    public StatusNotFoundException(String message) {
        super(message);
    }
}

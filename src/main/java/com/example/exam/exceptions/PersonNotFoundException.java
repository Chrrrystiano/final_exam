package com.example.exam.exceptions;

public class PersonNotFoundException extends RuntimeException {
    public PersonNotFoundException() {
    }

    public PersonNotFoundException(String message) {
        super(message);
    }
}

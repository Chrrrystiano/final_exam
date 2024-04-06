package com.example.exam.exceptions;

public class ImportStatusNotFoundException extends RuntimeException{
    public ImportStatusNotFoundException() {
    }

    public ImportStatusNotFoundException(String message) {
        super(message);
    }
}

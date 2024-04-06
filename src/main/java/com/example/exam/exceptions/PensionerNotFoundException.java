package com.example.exam.exceptions;

public class PensionerNotFoundException extends RuntimeException{
    public PensionerNotFoundException() {
    }

    public PensionerNotFoundException(String message) {
        super(message);
    }
}

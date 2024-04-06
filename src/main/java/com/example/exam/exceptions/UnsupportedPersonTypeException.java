package com.example.exam.exceptions;

public class UnsupportedPersonTypeException extends RuntimeException {
    public UnsupportedPersonTypeException(String type) {
        super("Unsupported person type: " + type);
    }
}

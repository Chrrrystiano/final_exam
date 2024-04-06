package com.example.exam.exceptions;

public class PermissionNotFoundException extends RuntimeException{
    public PermissionNotFoundException() {
    }

    public PermissionNotFoundException(String message) {
        super(message);
    }
}

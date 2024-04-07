package com.example.exam.exceptions;

public class MultipartToFileConverterException extends RuntimeException{
    public MultipartToFileConverterException() {
        super();
    }

    public MultipartToFileConverterException(String message) {
        super(message);
    }
}

package com.example.exam.exceptions;

public class FailedImportFileException extends RuntimeException{
    public FailedImportFileException() {
    }

    public FailedImportFileException(String message) {
        super(message);
    }

    public FailedImportFileException(String failedToImportCsvData, Exception e) {
    }
}

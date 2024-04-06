package com.example.exam.exceptions.others;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorDetails {
    private int code;
    private String status;
    private String message;
    private String uri;
    private String method;
}

package com.example.exam.jdbc.status;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImportResponse {
    private String message;
    private String taskId;
}

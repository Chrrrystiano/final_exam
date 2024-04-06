package com.example.exam.jdbc.status;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class ImportFileStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String taskId;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int processedRows;

    public ImportFileStatus(String taskId){
        this.taskId = taskId;
        this.status = Status.IN_PROGRESS;
        this.startTime = LocalDateTime.now();
        this.processedRows = 0;
    }
}

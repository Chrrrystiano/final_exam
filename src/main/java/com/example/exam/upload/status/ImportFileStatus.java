package com.example.exam.upload.status;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "IMPORTS")
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
}

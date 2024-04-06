package com.example.exam.jdbc.status;

import com.example.exam.exceptions.ImportStatusNotFoundException;
import com.example.exam.jdbc.CsvFileImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ImportFileStatusService {
    private final Logger logger = LoggerFactory.getLogger(ImportFileStatusService.class);

    private final ImportFileStatusRepository importFileStatusRepository;

    @Autowired
    public ImportFileStatusService(ImportFileStatusRepository importFileStatusRepository) {
        this.importFileStatusRepository = importFileStatusRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ImportFileStatus startImport(String taskId) {
        ImportFileStatus importFileStatus = new ImportFileStatus();
        importFileStatus.setTaskId(taskId);
        importFileStatus.setStatus(Status.IN_PROGRESS);
        importFileStatus.setStartTime(LocalDateTime.now());
        logger.info("Zapisywanie statusu importu, Task ID: {}", taskId);

        ImportFileStatus savedStatus = importFileStatusRepository.save(importFileStatus);
        logger.info("Status importu zapisany, Task ID: {}", taskId);

        return savedStatus;
    }

//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    public void updateImportStatus(String taskId, Status status, int processedRows) {
//        logger.info("Aktualizacja statusu importu, Task ID: {}", taskId);
//
//        ImportFileStatus importFileStatus = importFileStatusRepository.findByTaskId(taskId).orElseThrow(() -> new IllegalArgumentException("Invalid import file status taskId: " + taskId));
//        if (status != Status.IN_PROGRESS || importFileStatus.getEndTime() == null) {
//            importFileStatus.setStatus(status);
//            importFileStatus.setProcessedRows(processedRows);
//            if (status != Status.IN_PROGRESS) {
//                importFileStatus.setEndTime(LocalDateTime.now());
//            }
//            importFileStatusRepository.save(importFileStatus);
//        }
//    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateImportStatus(String taskId, int processedRows) {
        ImportFileStatus importFileStatus = importFileStatusRepository.findByTaskId(taskId)
                .orElseThrow(() -> new ImportStatusNotFoundException("Cannot find ImportStatus with taskId: " + taskId));
        importFileStatus.setProcessedRows(processedRows);
        importFileStatus.setStatus(Status.IN_PROGRESS);
        importFileStatusRepository.save(importFileStatus);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void finishImport(String taskId, int processedRows, boolean succes) {
        ImportFileStatus importFileStatus = importFileStatusRepository.findByTaskId(taskId)
                .orElseThrow(() -> new ImportStatusNotFoundException("Cannot find ImportStatus with taskId: " + taskId));
        importFileStatus.setProcessedRows(processedRows);
        importFileStatus.setEndTime(LocalDateTime.now());
        importFileStatus.setStatus(succes ? Status.COMPLETED : Status.REJECTED);
        importFileStatusRepository.save(importFileStatus);
    }

    public ImportFileStatus getImportFileStatus(String taskId) {
        return importFileStatusRepository.findByTaskId(taskId)
                .orElseThrow(() -> new ImportStatusNotFoundException("Cannot find ImportStatus with taskId: " + taskId));
    }
}
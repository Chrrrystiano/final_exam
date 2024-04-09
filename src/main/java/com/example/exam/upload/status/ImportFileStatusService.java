package com.example.exam.upload.status;

import com.example.exam.exceptions.ImportStatusNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ImportFileStatusService {

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
        ImportFileStatus savedStatus = importFileStatusRepository.save(importFileStatus);
        return savedStatus;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateImportStatusProcessedRows(String taskId, int processedRows) {
        ImportFileStatus importFileStatus = importFileStatusRepository.findByTaskId(taskId)
                .orElseThrow(() -> new ImportStatusNotFoundException("Cannot find ImportStatus with taskId: " + taskId));
        importFileStatus.setProcessedRows(processedRows);
        importFileStatus.setStatus(Status.IN_PROGRESS);
        importFileStatusRepository.save(importFileStatus);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void finishImport(String taskId, boolean succes) {
        ImportFileStatus importFileStatus = importFileStatusRepository.findByTaskId(taskId)
                .orElseThrow(() -> new ImportStatusNotFoundException("Cannot find ImportStatus with taskId: " + taskId));
        importFileStatus.setEndTime(LocalDateTime.now());
        importFileStatus.setStatus(succes ? Status.COMPLETED : Status.REJECTED);
        importFileStatusRepository.save(importFileStatus);
    }

    @Transactional(readOnly = true)
    public ImportFileStatus getImportFileStatus(String taskId) {
        return importFileStatusRepository.findByTaskId(taskId)
                .orElseThrow(() -> new ImportStatusNotFoundException("Cannot find ImportStatus with taskId: " + taskId));
    }
}
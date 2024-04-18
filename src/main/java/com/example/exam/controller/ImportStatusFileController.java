package com.example.exam.controller;

import com.example.exam.upload.status.ImportFileStatus;
import com.example.exam.upload.status.ImportFileStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/import")
public class ImportStatusFileController {
    private final ImportFileStatusService importFileStatusService;

    @Autowired
    public ImportStatusFileController(ImportFileStatusService importFileStatusService) {
        this.importFileStatusService = importFileStatusService;
    }

    @GetMapping("/{taskId}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('IMPORTER')")
    public ImportFileStatus getImportStatusFile(@PathVariable String taskId) {
        return importFileStatusService.getImportFileStatus(taskId);
    }
}

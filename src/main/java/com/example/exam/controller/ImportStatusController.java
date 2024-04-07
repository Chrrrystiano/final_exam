package com.example.exam.controller;

import com.example.exam.jdbc.status.ImportFileStatus;
import com.example.exam.jdbc.status.ImportFileStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/import")
public class ImportStatusController {
    private final ImportFileStatusService importFileStatusService;

    @Autowired
    public ImportStatusController(ImportFileStatusService importFileStatusService) {
        this.importFileStatusService = importFileStatusService;
    }

    @GetMapping("/status/{taskId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('IMPORTER')")
    public ImportFileStatus getImportStatus(@PathVariable String taskId) {
        return importFileStatusService.getImportFileStatus(taskId);
    }

}

package com.example.exam.controller;

import com.example.exam.upload.service.ImportFileService;
import com.example.exam.upload.status.ImportFileStatus;
import com.example.exam.upload.status.ImportFileStatusService;
import com.example.exam.upload.status.ImportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ImportFileController {
    private final ImportFileStatusService importFileStatusService;
    private final ImportFileService importFileService;

    @PostMapping("/imports")
    @PreAuthorize("hasRole('ADMIN') or hasRole('IMPORTER')")
    public ResponseEntity<ImportResponse> importFile(@RequestParam("file") MultipartFile file) throws IOException {
        String taskId = UUID.randomUUID().toString();
        ImportResponse response = new ImportResponse("The import has been accepted", taskId);
        importFileService.importFile(file, taskId);
        return new ResponseEntity<>(response, OK);
    }


    @GetMapping("/{taskId}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('IMPORTER')")
    public ImportFileStatus getImportStatusFile(@PathVariable String taskId) {
        return importFileStatusService.getImportFileStatus(taskId);
    }
}

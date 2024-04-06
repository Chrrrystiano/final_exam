//package com.example.exam.controller;
//
//import com.example.exam.kafka.ImportStatus;
//import com.example.exam.kafka.service.ImportStatusService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/import")
//public class ImportStatusController {
//    private final ImportStatusService importStatusService;
//
//    @Autowired
//    public ImportStatusController(ImportStatusService importStatusService) {
//        this.importStatusService = importStatusService;
//    }
//
//    @GetMapping("/status/{taskId}")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('IMPORTER')")
//    public ImportStatus getImportStatus(@PathVariable String taskId) {
//        return importStatusService.getImportStatus(taskId);
//    }
//
//}

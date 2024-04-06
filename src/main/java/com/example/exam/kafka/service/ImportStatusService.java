//package com.example.exam.kafka.service;
//
//import com.example.exam.exceptions.ImportStatusNotFoundException;
//import com.example.exam.kafka.ImportStatus;
//import com.example.exam.kafka.ImportStatusRepository;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.concurrent.locks.ReentrantLock;
//
//@Service
//public class ImportStatusService {
//    private final ReentrantLock lock = new ReentrantLock();
//    private final ImportStatusRepository repository;
//    private final ConcurrentHashMap<String, AtomicInteger> taskUpdates = new ConcurrentHashMap<>();
//
//    @Autowired
//    public ImportStatusService(ImportStatusRepository repository) {
//        this.repository = repository;
//    }
//
//    @Transactional
//    public void createOrUpdateImportStatus(ImportStatus importStatus) {
//        repository.save(importStatus);
//    }
//
//    public void aggregateProcessedRows(String taskId) {
//        lock.lock();
//        try {
//            taskUpdates.computeIfAbsent(taskId, k -> new AtomicInteger(0)).incrementAndGet();
//        } finally {
//            lock.unlock();
//        }
//    }
//
//    @Scheduled(fixedDelay = 2000)
//    public void applyUpdates() {
//        lock.lock();
//        try {
//            taskUpdates.forEach((taskId, count) -> {
//                ImportStatus status = repository.findByTaskId(taskId)
//                        .orElseThrow(() -> new RuntimeException("Cannot find ImportStatus with taskId: " + taskId));
//                if (status != null) {
//                    int newProcessedRows = status.getProcessedRows() + count.get();
//                    status.setProcessedRows(newProcessedRows);
//                    if (newProcessedRows >= status.getTotalRows() && status.getStatus().equals("STARTED") && status.getEndTime() == null) {
//                        status.setStatus("COMPLETED");
//                        status.setEndTime(LocalDateTime.now());
//                    }
//                    repository.save(status);
//                    count.set(0);
//                }
//            });
//        } finally {
//            lock.unlock();
//        }
//    }
//
//    public ImportStatus getImportStatus(String taskId) {
//        ImportStatus status = repository.findByTaskId(taskId)
//                .orElseThrow(() -> new ImportStatusNotFoundException("Cannot find ImportStatus with taskId: " + taskId));
//        AtomicInteger inMemoryUpdate = taskUpdates.get(taskId);
//        if (status != null && inMemoryUpdate != null) {
//            status.setProcessedRows(status.getProcessedRows() + inMemoryUpdate.get());
//        }
//        return status;
//    }
//}

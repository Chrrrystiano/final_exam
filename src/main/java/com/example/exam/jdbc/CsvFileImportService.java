package com.example.exam.jdbc;

import com.example.exam.exceptions.NotSavedException;
import com.example.exam.exceptions.UnsupportedPersonTypeException;
import com.example.exam.jdbc.status.ImportFileStatusService;
import com.opencsv.CSVReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.sql.SQLException;
import java.util.*;


@Service
public class CsvFileImportService {
    private final Logger logger = LoggerFactory.getLogger(CsvFileImportService.class);

    private final ImportFileStatusService importFileStatusService;
    private final JdbcTemplate jdbcTemplate;
    private final CacheManager cacheManager;
    private final PersonImportService personImportService;

    @Value("${batch.size}")
    private int batchSize;

    @Autowired
    public CsvFileImportService(CacheManager cacheManager, ImportFileStatusService importFileStatusService, JdbcTemplate jdbcTemplate, PersonImportService personImportService) {
        this.importFileStatusService = importFileStatusService;
        this.jdbcTemplate = jdbcTemplate;
        this.cacheManager = cacheManager;
        this.personImportService = personImportService;
    }

    @Transactional
    @Async("taskExecutor")
    public void importCsv(File file, String taskId) {
        importFileStatusService.startImport(taskId);
        try (CSVReader csvReader = new CSVReader(new FileReader(file))) {
            csvReader.readNext();
            List<String[]> batch = new ArrayList<>(batchSize);
            for (String[] csvRow : csvReader) {
                batch.add(csvRow);
                if (batch.size() == batchSize) {
                    savePeople(batch, taskId);
                }
            }
            if (!batch.isEmpty()) {
                savePeople(batch, taskId);
            }
            importFileStatusService.finishImport(taskId, true);
        } catch (Exception e) {
            importFileStatusService.finishImport(taskId, false);
            logger.error("ERROR during saving person {}", e.getMessage());
            throw new RuntimeException();
        } finally {
            if (!file.delete()) {
                logger.error("ERROR: Deleting temp file: {}", file.getAbsolutePath());
            }
        }
    }

    public void savePeople(List<String[]> batch, String taskId) {
        int processedRows = 0;
        try {
            for (String[] csvData : batch) {
                PersonCreationStrategyJDBC strategy = personImportService.findPersonCreationStrategyJDBC(csvData);
                if (strategy != null) {
                    strategy.createPerson(csvData, jdbcTemplate);
                    processedRows++;
                    importFileStatusService.updateImportStatusProcessedRows(taskId, processedRows);
                } else {
                    importFileStatusService.finishImport(taskId, false);
                    throw new UnsupportedPersonTypeException("Unsupported type: " + csvData[0]);
                }
            }
            batch.clear();
            clearCache();

        } catch (RuntimeException | SQLException e) {
            logger.error("ERROR during saving person {}", e.getMessage());
            importFileStatusService.finishImport(taskId, false);
            throw new NotSavedException(e.getMessage());
        }
    }

    private void clearCache() {
        for (String cacheName : cacheManager.getCacheNames()) {
            Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
        }
    }
}
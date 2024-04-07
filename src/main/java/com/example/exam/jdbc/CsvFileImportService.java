package com.example.exam.jdbc;

import com.example.exam.exceptions.UnsupportedPersonTypeException;
import com.example.exam.jdbc.status.ImportFileStatusService;
import com.example.exam.model.person.Person;
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

    private final List<PersonCreationStrategyJDBC> strategies;
    private final ImportFileStatusService importFileStatusService;
    private final JdbcTemplate jdbcTemplate;
    private final CacheManager cacheManager;

    @Value("${batch.size}")
    private int batchSize;

    @Autowired
    public CsvFileImportService(CacheManager cacheManager, List<PersonCreationStrategyJDBC> strategies, ImportFileStatusService importFileStatusService, JdbcTemplate jdbcTemplate) {
        this.strategies = strategies;
        this.importFileStatusService = importFileStatusService;
        this.jdbcTemplate = jdbcTemplate;
        this.cacheManager = cacheManager;
    }

    @Transactional
    @Async("taskExecutor")
    public void importCsv(File file, String taskId) {
        importFileStatusService.startImport(taskId);
        int processedRows = 0;
        try (CSVReader csvReader = new CSVReader(new FileReader(file))) {
            csvReader.readNext();

            List<String[]> batch = new ArrayList<>(batchSize);
            for (String[] csvRow : csvReader) {
                processedRows++;
                importFileStatusService.updateImportStatusProcessedRows(taskId, processedRows);

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
            logger.error("ERROR: {}", e.getMessage());
        } finally {
            if (!file.delete()) {
                logger.error("ERROR: Deleting temp file: {}", file.getAbsolutePath());
            }
        }
    }

    public void savePeople(List<String[]> batch, String taskId) {
        try {
            for (String[] csvData : batch) {
                PersonCreationStrategyJDBC strategy = findStrategy(csvData[0]);
                if (strategy != null) {
                    strategy.createPerson(csvData, jdbcTemplate);
                } else {
                    importFileStatusService.finishImport(taskId, false);
                    throw new UnsupportedPersonTypeException("Unsupported type: " + csvData[0]);
                }
            }
            batch.clear();
            clearCache();

        } catch (RuntimeException e) {
            logger.error("Error during saving person:", e);
            importFileStatusService.finishImport(taskId, false);
            throw e;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void clearCache() {
        for (String cacheName : cacheManager.getCacheNames()) {
            Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
        }
    }

    private PersonCreationStrategyJDBC findStrategy(String type) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(type))
                .findFirst()
                .orElse(null);
    }
}
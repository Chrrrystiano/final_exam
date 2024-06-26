package com.example.exam.upload.service;

import com.example.exam.exceptions.NotSavedException;
import com.example.exam.exceptions.UnsupportedPersonTypeException;
import com.example.exam.model.person.Person;
import com.example.exam.upload.status.ImportFileStatusService;
import com.example.exam.upload.strategies.PersonCreationStrategyJDBC;
import com.opencsv.CSVReader;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CsvFileImportService {

    private final Logger logger = LoggerFactory.getLogger(CsvFileImportService.class);

    private final ImportFileStatusService importFileStatusService;
    private final JdbcTemplate jdbcTemplate;
    private final CacheManager cacheManager;
    private final PersonImportService personImportService;

    @Value("${batch.size}")
    private int batchSize;

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
                batch.add(csvRow);
                if (batch.size() == batchSize) {
                    savePeople(batch, taskId);
                }
            }
            if (!batch.isEmpty()) {
                savePeople(batch, taskId);
            }
            importFileStatusService.updateImportStatusProcessedRows(taskId, processedRows);
            importFileStatusService.finishImport(taskId, true);
        } catch (Exception e) {
            logger.error("ERROR during saving person {}", e.getMessage());
            importFileStatusService.finishImport(taskId, false);
        } finally {
            if (!file.delete()) {
                logger.error("ERROR: Deleting temp file: {}", file.getAbsolutePath());
            }
        }
    }

    public void savePeople(List<String[]> batch, String taskId) {
        Map<String, List<String[]>> groupedByType = batch.stream()
                .collect(Collectors.groupingBy(row -> row[0]));
        groupedByType.forEach((type, rows) -> {
            try {
                PersonCreationStrategyJDBC strategy = personImportService.findPersonCreationStrategyJDBC(rows.get(0));
                if (strategy != null) {
                    strategy.savePeopleFromBatch(rows, jdbcTemplate);

                } else {
                    throw new UnsupportedPersonTypeException("Unsupported type: " + type);
                }
            } catch (SQLException e) {
                logger.error("ERROR during batch save for type {}: {}", type, e.getMessage(), e);
                importFileStatusService.finishImport(taskId, false);
                throw new NotSavedException(e.getMessage());
            }
        });
        batch.clear();
        clearCache();
    }


    private void clearCache() {
        for (String cacheName : cacheManager.getCacheNames()) {
            Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
        }
    }
}
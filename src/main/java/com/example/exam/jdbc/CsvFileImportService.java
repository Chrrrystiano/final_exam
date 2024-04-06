package com.example.exam.jdbc;

import com.example.exam.exceptions.FailedImportFileException;
import com.example.exam.exceptions.InvalidDataFileException;
import com.example.exam.exceptions.UnsupportedPersonTypeException;
import com.example.exam.jdbc.status.ImportFileStatus;
import com.example.exam.jdbc.status.ImportFileStatusRepository;
import com.example.exam.jdbc.status.ImportFileStatusService;
import com.example.exam.jdbc.status.Status;
import com.example.exam.model.person.Person;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import static java.io.File.createTempFile;

@Service
public class CsvFileImportService {
    private final Logger logger = LoggerFactory.getLogger(CsvFileImportService.class);

    private final List<PersonCreationStrategyJDBC> strategies;
    private final ImportFileStatusService importFileStatusService;
    private final JdbcTemplate jdbcTemplate;

    private TransactionTemplate transactionTemplate;
    private Map<String, ImportFileStatus> importStatusMap = new ConcurrentHashMap<>();
    private final CacheManager cacheManager;

    @Value("${batch.size}")
    private int batchSize;

    @Autowired
    public CsvFileImportService(CacheManager cacheManager, List<PersonCreationStrategyJDBC> strategies, TransactionTemplate transactionTemplate, ImportFileStatusService importFileStatusService, JdbcTemplate jdbcTemplate) {
        this.strategies = strategies;
        this.importFileStatusService = importFileStatusService;
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = transactionTemplate;
        this.cacheManager = cacheManager;
    }

    public void importFile(MultipartFile multipartFile, String taskId) throws IOException {
        File file;
        file = multipartFileToFileConverter(multipartFile);
        importCsv(file, taskId);
    }

    private File multipartFileToFileConverter(MultipartFile multipartFile) throws IOException {
        File file = createTempFile("temp", null);
        multipartFile.transferTo(file);
        file.deleteOnExit();
        return file;
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
                importFileStatusService.updateImportStatus(taskId, processedRows);
                batch.add(csvRow);
                if (batch.size() == batchSize) {
                    savePeople(batch);
                }
            }
            if (!batch.isEmpty()) {
                savePeople(batch);
            }

        } catch (IOException | CsvValidationException | SQLException e) {
            logger.error("ERROR: {}", e.getMessage());
            importFileStatusService.finishImport(taskId, processedRows, false);

        } catch (Exception e) {
            importFileStatusService.finishImport(taskId, processedRows, false);
        } finally {
            if (!file.delete()) {
                logger.error("ERROR: Deleting temp file: {}", file.getAbsolutePath());
            }
        }
        importFileStatusService.finishImport(taskId, processedRows, true);
    }


    public void savePeople(List<String[]> batch) throws Exception {
        try {
            for (String[] csvData : batch) {
                PersonCreationStrategyJDBC strategy = findStrategy(csvData[0]);
                if (strategy != null) {
                    strategy.createPerson(csvData, jdbcTemplate);
                } else {
                    throw new UnsupportedPersonTypeException("Unsupported type: " + csvData[0]);
                }
            }
            batch.clear();
            clearCache();

        } catch (RuntimeException e) {
            logger.error("Error during saving person {}:", e.getMessage());
            throw e;
        }
    }

    private PersonCreationStrategyJDBC findStrategy(String type) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(type))
                .findFirst()
                .orElse(null);
    }

    private void clearCache() {
        for (String cacheName : cacheManager.getCacheNames()) {
            Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
        }
    }


//    @Transactional// W METODZIE PODGLĄD NA LICZBĘ PRZETWORZONYCH WIERSZY JEST NA KOŃCU, COFA TYLKO WADLIWY BATCH USTAWIA REJECTED I ZATRZYMUJE IMPORT
//    @Async
//    public void importCsv(MultipartFile file, String taskId) {
//        AtomicInteger totalRowsProcessed = new AtomicInteger(0);
//        AtomicBoolean anyBatchFailed = new AtomicBoolean(false);
//        int batchSize = 500;
//
//        List<CompletableFuture<Void>> futures = new ArrayList<>();
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
//            reader.readLine(); // Pominięcie nagłówka CSV
//            List<String[]> batch = new ArrayList<>(batchSize);
//            String line;
//            importFileStatusService.startImport(taskId);
//            while ((line = reader.readLine()) != null) {
//                batch.add(line.split(","));
//                if (batch.size() == batchSize) {
//                    futures.add(processBatchAsync(new ArrayList<>(batch), totalRowsProcessed, anyBatchFailed));
//                    batch.clear();
//                }
//            }
//            if (!batch.isEmpty()) {
//                futures.add(processBatchAsync(batch, totalRowsProcessed, anyBatchFailed));
//            }
//
//            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
//
//            if (anyBatchFailed.get()) {
//                importFileStatusService.updateImportStatus(taskId, totalRowsProcessed.get());
//                throw new RuntimeException("Some batches failed to process");
//            }
//
//            importFileStatusService.updateImportStatus(taskId, totalRowsProcessed.get());
//
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to import CSV", e);
//        } finally {
//            importFileStatusService.finishImport(taskId, totalRowsProcessed.get(), !anyBatchFailed.get());
//        }
//    }

//    private CompletableFuture<Void> processBatchAsync(List<String[]> batch, AtomicInteger totalRowsProcessed, AtomicBoolean anyBatchFailed) {
//        return CompletableFuture.runAsync(() -> {
//            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
//            def.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);
//            TransactionStatus status = transactionManager.getTransaction(def);
//            try {
//                for (String[] csvData : batch) {
//                    PersonCreationStrategyJDBC strategy = findStrategy(csvData[0]);
//                    if (strategy != null) {
//                        strategy.createPerson(csvData, jdbcTemplate);
//                        totalRowsProcessed.incrementAndGet();
//                    } else {
//                        anyBatchFailed.set(true);
//                        break;
//                    }
//                }
//                transactionManager.commit(status);
//            } catch (Exception e) {
//                transactionManager.rollback(status);
//                anyBatchFailed.set(true);
//            }
//        }, taskExecutor);
//    }


//    @Transactional // TA METODA MOZNA POWIEDZIEC ZE DZIALA TYLKO ASYNC PIERDOLI WSZYSTKO
//    @Async("taskExecutor")
//    public void importCsv(MultipartFile file, String taskId) {
//        int rows = 0;
//        importFileStatusService.startImport(taskId);
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
//            reader.readLine();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                String[] csvData = line.split(",");
//                PersonCreationStrategyJDBC strategy = findStrategy(csvData[0]);
//                if (strategy != null) {
//                    strategy.createPerson(csvData, jdbcTemplate);
//                    rows++;
//                } else {
//                    importFileStatusService.finishImport(taskId, rows, false);
//                    throw new UnsupportedPersonTypeException("Unsupported type: " + csvData[0]);
//                }
//            }
//            importFileStatusService.finishImport(taskId, rows, true);
//        } catch (Exception e) {
//            importFileStatusService.finishImport(taskId, rows, false);
//            throw new RuntimeException("Failed to import CSV data", e);
//        } finally {
//            if (!file.delete()) {
//                logger.error("ERROR: Deleting temp file: {}", file.getAbsolutePath());
//            }
//        }
//    }
    //prywatna metoda, z multipartfile na file zrobic converter

//    @Async // PIERWOWZÓR
//    public void importCsv(MultipartFile file, String taskId) {
//        logger.info("Rozpoczęcie importu pliku CSV, Task ID: {}", taskId);
//        logger.info("Otrzymano plik do importu: {}", file.getOriginalFilename());
//        ImportFileStatus importFileStatus = importFileStatusService.startImport(taskId);
//        logger.info("Status importu rozpoczęty, Task ID: {}", taskId);
//        int processedRows = 0;
//
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
//            logger.info("Otwarcie pliku CSV, Task ID: {}", taskId);
//            logger.info("Otwieranie strumienia dla pliku: {}", file.getOriginalFilename());
//            reader.readLine();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                logger.debug("Przetwarzanie wiersza: {}", line);
//                String[] csvData = line.split(",");
//                PersonCreationStrategyJDBC strategy = findStrategy(csvData[0]);
//                if (strategy != null) {
//                    strategy.createPerson(csvData, jdbcTemplate);
//                    processedRows++;
//                    logger.debug("Wiersz {} przetworzony pomyślnie.", processedRows);
//                } else {
//                    logger.warn("Nieznane typ osoby: {}", csvData[0]);
//                    throw new UnsupportedPersonTypeException("Unsupported type: " + csvData[0]);
//                }
//            }
//            importFileStatusService.updateImportStatus(importFileStatus.getTaskId(), Status.COMPLETED, processedRows);
//            logger.info("Zakończono import pliku CSV, Task ID: {}, przetworzone wiersze: {}", taskId, processedRows);
//        } catch (Exception e) {
//            logger.error("Błąd podczas importu CSV, Task ID: {}, plik: {}", taskId, file.getOriginalFilename(), e);
//            importFileStatusService.updateImportStatus(importFileStatus.getTaskId(), Status.REJECTED, processedRows);
//            throw new RuntimeException("Failed to import CSV data", e);
//        }
//        logger.info("Zakończono import pliku CSV, Task ID: {}, przetworzone wiersze: {}", taskId, processedRows);
//    }


//    private PersonCreationStrategyJDBC findStrategy(String type) {
//        return strategies.stream()
//                .filter(strategy -> strategy.supports(type))
//                .findFirst()
//                .orElse(null);
//    }


}
//package com.example.exam.kafka.service;
//
//import com.example.exam.kafka.ImportStatus;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.apache.commons.csv.CSVFormat;
//import org.apache.commons.csv.CSVParser;
//import org.apache.commons.csv.CSVRecord;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.nio.charset.StandardCharsets;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//
//@Service
//public class FileProcessingService {
//
//    private final KafkaProducerService kafkaProducerService;
//    private final KafkaTemplate<String, String> kafkaTemplate;
//
//    private final ObjectMapper objectMapper;
//    private Map<String, ImportStatus> importStatusMap = new ConcurrentHashMap<>();
//    private final ImportStatusService importStatusService;
//
//    private static final Logger logger = LoggerFactory.getLogger(FileProcessingService.class);
//
//    @Autowired
//    public FileProcessingService(KafkaProducerService kafkaProducerService, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper, ImportStatusService importStatusService) {
//        this.kafkaProducerService = kafkaProducerService;
//        this.kafkaTemplate = kafkaTemplate;
//        this.objectMapper = objectMapper;
//        this.importStatusService = importStatusService;
//    }
//
//    @Transactional("platformTransactionManagerChained")
//    public void processFile(MultipartFile file, String taskId) throws IOException {
//        logger.info("ZACZYNAM METODE PROCESSFILE");
//        ImportStatus importStatus = new ImportStatus();
//        importStatus.setTaskId(taskId);
//        importStatus.setStatus("STARTED");
//        importStatus.setStartTime(java.time.LocalDateTime.now());
//        importStatus.setProcessedRows(0);
//        logger.info("USTAWILEM POCZATKOWE DANE IMPORTU. TERAZ BEDE CZYTAC PLIK");
//
//        BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
//        CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
//        logger.info("PRZECZYTALEM PLIK");
//        int processedRows = 0;
//        logger.info("ZACZYNAM TRY");
//        try {
//            for (CSVRecord record : csvParser) {
//                String jsonRecord = convertRecordToJson(record, taskId);
//                kafkaTemplate.send("topicName", jsonRecord);
//                processedRows++;
//                logger.info("IDZIE");
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to process file", e);
//
//        }
//
//        importStatus.setTotalRows(processedRows);
//        logger.info("ZAKTUALIZOWAŁEM PROCESSEDROWS");
//        importStatusService.createOrUpdateImportStatus(importStatus);
//    }
//
//    private String convertRecordToJson(CSVRecord record, String taskId) throws IOException {
//        Map<String, String> recordMap = record.toMap();
//        recordMap.put("taskId", taskId);
//        return objectMapper.writeValueAsString(recordMap);
//    }
//}

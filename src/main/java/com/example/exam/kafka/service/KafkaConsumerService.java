//package com.example.exam.kafka.service;
//
//import com.example.exam.exceptions.UnrecognizedTypeException;
//import com.example.exam.strategies.PersonCreationStrategy;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class KafkaConsumerService {
//    private final ImportStatusService importStatusService;
//    private ObjectMapper objectMapper;
//    private List<PersonCreationStrategy> strategies;
//    private Map<String, PersonCreationStrategy> strategyMap = new HashMap<>();
//
//    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
//
//    @Autowired
//    public KafkaConsumerService(ObjectMapper objectMapper, ImportStatusService importStatusService, List<PersonCreationStrategy> strategies) {
//        this.objectMapper = objectMapper;
//        this.importStatusService = importStatusService;
//        this.strategies = strategies;
//    }
//
//    @Async
//    @KafkaListener(topics = "${topic.name}", groupId = "your-group-id")
//    public void consume(String message) throws IOException {
//        logger.info("ZACZYNAM METODE CONSUME");
//        Map<String, Object> recordMap = objectMapper.readValue(message, Map.class);
//        String typeRecord = ((String) recordMap.get("type")).toUpperCase();
//        String taskId = (String) recordMap.get("taskId");
//        logger.info("WYBIERAM STRATEGIE");
//        PersonCreationStrategy personCreationStrategy = strategies.stream()
//                .filter(x -> x.supports(typeRecord))
//                .findFirst()
//                .orElseThrow(() -> new UnrecognizedTypeException("Unrecognized type: " + typeRecord));
//        logger.info("WYBRAŁEM STRATEGIE I MAPUJE");
//        personCreationStrategy.save(recordMap);
//
//        importStatusService.aggregateProcessedRows(taskId);
//    }
//}

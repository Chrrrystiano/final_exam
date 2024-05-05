package com.example.exam.upload.service;

import com.example.exam.exceptions.UnsupportedPersonTypeException;
import com.example.exam.upload.strategies.PersonCreationStrategyJDBC;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonImportService {
    private final List<PersonCreationStrategyJDBC> strategyJDBCList;

    public PersonCreationStrategyJDBC findPersonCreationStrategyJDBC(String[] csvRow) {
        return strategyJDBCList.stream()
                .filter(s -> s.supports(csvRow[0]))
                .findFirst()
                .orElseThrow(() -> new UnsupportedPersonTypeException("Unsupported type: " + csvRow[0]));
    }
}
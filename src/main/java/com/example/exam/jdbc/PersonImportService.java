package com.example.exam.jdbc;

import com.example.exam.exceptions.EmailValidationException;
import com.example.exam.exceptions.PeselValidationException;
import com.example.exam.exceptions.UnsupportedPersonTypeException;
import com.example.exam.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonImportService {
    private final List<PersonCreationStrategyJDBC> strategyJDBCList;
    private final PersonRepository personRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PersonImportService(List<PersonCreationStrategyJDBC> strategyJDBCList, PersonRepository personRepository, JdbcTemplate jdbcTemplate) {
        this.strategyJDBCList = strategyJDBCList;
        this.personRepository = personRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public PersonCreationStrategyJDBC findPersonCreationStrategyJDBC(String[] csvRow) {
        String type = csvRow[0];
        PersonCreationStrategyJDBC strategy = strategyJDBCList.stream()
                .filter(s -> s.supports(type))
                .findFirst()
                .orElseThrow(() -> new UnsupportedPersonTypeException("Unsupported type: " + type));

        if (personRepository.existingPesel(csvRow[3])) {
            throw new PeselValidationException("Wrong PESEL number. This PESEL is already in the database.");
        }
        if (personRepository.existingEmail(csvRow[6])) {
            throw new EmailValidationException("Wrong EMAIL number. This EMAIL is already in the database.");
        }

        return strategy;
    }
}
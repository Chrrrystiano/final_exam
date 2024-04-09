package com.example.exam.upload.service;

import com.example.exam.exceptions.EmailValidationException;
import com.example.exam.exceptions.PeselValidationException;
import com.example.exam.exceptions.UnsupportedPersonTypeException;
import com.example.exam.repository.PersonRepository;
import com.example.exam.upload.strategies.PersonCreationStrategyJDBC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonImportService {
    private final List<PersonCreationStrategyJDBC> strategyJDBCList;
    private final PersonRepository personRepository;

    @Autowired
    public PersonImportService(List<PersonCreationStrategyJDBC> strategyJDBCList, PersonRepository personRepository) {
        this.strategyJDBCList = strategyJDBCList;
        this.personRepository = personRepository;
    }

    public PersonCreationStrategyJDBC findPersonCreationStrategyJDBC(String[] csvRow) {
        return strategyJDBCList.stream()
                .filter(s -> s.supports(csvRow[0]))
                .findFirst()
                .orElseThrow(() -> new UnsupportedPersonTypeException("Unsupported type: " + csvRow[0]));
    }

    public void checkPeselAndEmailInCsvRow(String[] csvRow) {
        if (personRepository.existingPesel(csvRow[3])) {
            throw new PeselValidationException("Wrong PESEL number. This PESEL is already in the database.");
        }
        if (personRepository.existingEmail(csvRow[6])) {
            throw new EmailValidationException("Wrong EMAIL number. This EMAIL is already in the database.");
        }
    }

}
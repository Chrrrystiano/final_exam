package com.example.exam.jdbc;

import com.example.exam.exceptions.EmailValidationException;
import com.example.exam.exceptions.PeselValidationException;
import com.example.exam.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Collections;

@Component
public class EmployeeCreationStrategyJDBC implements PersonCreationStrategyJDBC {

    private final PersonRepository personRepository;

    @Autowired
    public EmployeeCreationStrategyJDBC(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public boolean supports(String type) {
        return "EMPLOYEE".equals(type);
    }

    @Override
    public void createPerson(String[] csvData, JdbcTemplate jdbcTemplate) throws SQLException {
        if (personRepository.existingPesel(csvData[3])) {
            throw new PeselValidationException("Wrong PESEL number. This PESEL is already in the database.");
        }
        if (personRepository.existingEmail(csvData[6])) {
            throw new EmailValidationException("Wrong EMAIL number. This EMAIL is already in the database.");
        }
        String sql = "INSERT INTO persons (type, name, surname, pesel, height, weight, email, current_position_start_date, current_salary, current_position) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, Collections.singletonList(new Object[]{
                "EMPLOYEE",
                csvData[1],
                csvData[2],
                csvData[3],
                Double.parseDouble(csvData[4]),
                Double.parseDouble(csvData[5]),
                csvData[6],
                Date.valueOf(csvData[7]),
                new BigDecimal(csvData[8]),
                csvData[9]}));
    }


}
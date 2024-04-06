package com.example.exam.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collections;

@Component
public class StudentCreationStrategyJDBC implements PersonCreationStrategyJDBC {
    @Override
    public boolean supports(String type) {
        return "STUDENT".equals(type);
    }

    @Override
    public void createPerson(String[] csvData, JdbcTemplate jdbcTemplate) throws SQLException {
        String sql = "INSERT INTO persons (type, name, surname, pesel, height, weight, email, university_name, year_of_study, field_of_study, scholarship_amount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, Collections.singletonList(new Object[]{
                "STUDENT",
                csvData[1],
                csvData[2],
                csvData[3],
                Double.parseDouble(csvData[4]),
                Double.parseDouble(csvData[5]),
                csvData[6],
                csvData[12],
                Integer.parseInt(csvData[13]),
                csvData[14],
                new BigDecimal(csvData[15])}));
    }
}




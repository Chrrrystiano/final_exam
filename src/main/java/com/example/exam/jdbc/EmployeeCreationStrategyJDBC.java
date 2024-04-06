package com.example.exam.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Collections;

@Component
public class EmployeeCreationStrategyJDBC implements PersonCreationStrategyJDBC {
    @Override
    public boolean supports(String type) {
        return "EMPLOYEE".equals(type);
    }

    @Override
    public void createPerson(String[] csvData, JdbcTemplate jdbcTemplate) throws SQLException {
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
package com.example.exam.upload.strategies;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class EmployeeCreationStrategyJDBC implements PersonCreationStrategyJDBC {
    @Override
    public boolean supports(String type) {
        return "EMPLOYEE".equals(type);
    }

    @Override
    public void savePeopleFromBatch(List<String[]> batchData, JdbcTemplate jdbcTemplate) {
        String sql = "INSERT INTO persons (type, name, surname, pesel, height, weight, email, current_position_start_date, current_salary, current_position) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();
        for (String[] csvData : batchData) {
            Object[] sqlArgs = new Object[]{
                    csvData[0],
                    csvData[1],
                    csvData[2],
                    csvData[3],
                    Double.parseDouble(csvData[4]),
                    Double.parseDouble(csvData[5]),
                    csvData[6],
                    LocalDate.parse(csvData[7]),
                    new BigDecimal(csvData[8]),
                    csvData[9]
            };
            batchArgs.add(sqlArgs);
        }
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }
}
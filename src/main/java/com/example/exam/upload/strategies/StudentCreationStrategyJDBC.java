package com.example.exam.upload.strategies;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class StudentCreationStrategyJDBC implements PersonCreationStrategyJDBC {

    @Override
    public boolean supports(String type) {
        return "STUDENT".equals(type);
    }

    @Override
    public void savePeopleFromBatch(List<String[]> batchData, JdbcTemplate jdbcTemplate) {
        String sql = "INSERT INTO persons (type, name, surname, pesel, height, weight, email, university_name, year_of_study, field_of_study, scholarship_amount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
                    csvData[12],
                    Integer.parseInt(csvData[13]),
                    csvData[14],
                    new BigDecimal(csvData[15])
            };
            batchArgs.add(sqlArgs);
        }
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }
}




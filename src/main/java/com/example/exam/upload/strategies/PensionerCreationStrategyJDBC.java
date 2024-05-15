package com.example.exam.upload.strategies;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class PensionerCreationStrategyJDBC implements PersonCreationStrategyJDBC {

    @Override
    public void savePeopleFromBatch(List<String[]> batchData, JdbcTemplate jdbcTemplate) {
        String sql = "INSERT INTO persons (type, name, surname, pesel, height, weight, email, pension_amount, years_of_work) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
                    new BigDecimal(csvData[10]),
                    Integer.parseInt(csvData[11])
            };
            batchArgs.add(sqlArgs);
        }
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    @Override
    public boolean supports(String type) {
        return "PENSIONER".equals(type);
    }
}



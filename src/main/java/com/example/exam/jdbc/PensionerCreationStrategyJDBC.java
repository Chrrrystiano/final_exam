package com.example.exam.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collections;

@Component
public class PensionerCreationStrategyJDBC implements PersonCreationStrategyJDBC {
    @Override
    public boolean supports(String type) {
        return "PENSIONER".equals(type);
    }

    @Override
    public void createPerson(String[] csvData, JdbcTemplate jdbcTemplate) throws SQLException {
        String sql = "INSERT INTO persons (type, name, surname, pesel, height, weight, email, pension_amount, years_of_work) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, Collections.singletonList(new Object[]{
                "PENSIONER",
                csvData[1],
                csvData[2],
                csvData[3],
                Double.parseDouble(csvData[4]),
                Double.parseDouble(csvData[5]),
                csvData[6],
                new BigDecimal(csvData[10]),
                Integer.parseInt(csvData[11])}));

    }

}


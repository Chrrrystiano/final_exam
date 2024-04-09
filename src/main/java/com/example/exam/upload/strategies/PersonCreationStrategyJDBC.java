package com.example.exam.upload.strategies;

import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.util.List;

public interface PersonCreationStrategyJDBC {


    boolean supports(String type);

    void savePeopleFromBatch(List<String[]> batchData, JdbcTemplate jdbcTemplate) throws SQLException;
}

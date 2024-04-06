package com.example.exam.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;

public interface PersonCreationStrategyJDBC {
    boolean supports(String type);

    void createPerson(String[] csvData, JdbcTemplate jdbcTemplate) throws SQLException;
}

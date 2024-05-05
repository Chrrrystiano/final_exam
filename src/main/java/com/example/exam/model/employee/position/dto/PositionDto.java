package com.example.exam.model.employee.position.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PositionDto {
    Long id;
    String name;
    Long employeeId;
    LocalDate startDate;
    LocalDate endDate;
    BigDecimal salary;
}

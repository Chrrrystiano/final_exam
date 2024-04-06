package com.example.exam.model.employee.position.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
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
    @Column(name = "name")
    @NotBlank(message = "The POSITION NAME field cannot be left empty")
    String name;
    Long employeeId;
    @Column(name = "start_date")
    @NotNull(message = "The START DATE field cannot be null")
    @PastOrPresent(message = "The start date must be in the past or present")
    LocalDate startDate;
    @Column(name = "end_date")
    @PastOrPresent(message = "The end date must be in the past or present")
    LocalDate endDate;
    @Column(name = "salary")
    @NotNull(message = "The SALARY field cannot be null")
    @PositiveOrZero(message = "Salary must be positive or zero")
    BigDecimal salary;
}

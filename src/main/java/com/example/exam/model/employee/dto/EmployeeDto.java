package com.example.exam.model.employee.dto;

import com.example.exam.model.employee.position.dto.PositionDto;
import com.example.exam.model.person.dto.PersonDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class EmployeeDto extends PersonDto {
    List<PositionDto> positions;
    LocalDate currentPositionStartDate;
    String currentPosition;
    private BigDecimal currentSalary;
}

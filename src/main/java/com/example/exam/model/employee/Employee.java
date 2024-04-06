package com.example.exam.model.employee;

import com.example.exam.model.employee.position.Position;
import com.example.exam.model.person.Person;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@DiscriminatorValue("EMPLOYEE")
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Employee extends Person {
    @JsonManagedReference
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Position> positions;
    @JsonProperty("current_position_start_date")
    private LocalDate currentPositionStartDate;
    @JsonProperty("current_salary")
    private BigDecimal currentSalary;
    @JsonProperty("current_position")
    private String currentPosition;
}

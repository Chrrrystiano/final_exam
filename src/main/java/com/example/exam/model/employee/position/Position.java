package com.example.exam.model.employee.position;

import com.example.exam.model.employee.Employee;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "POSITION")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    @NotBlank(message = "The POSITION NAME field cannot be left empty")
    private String name;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    private Employee employee;
    @Column(name = "start_date")
    @NotNull(message = "The START DATE field cannot be null")
    @PastOrPresent(message = "The start date must be in the past or present")
    private LocalDate startDate;
    @Column(name = "end_date")
    @PastOrPresent(message = "The end date must be in the past or present")
    private LocalDate endDate;
    @Column(name = "salary")
    @NotNull(message = "The SALARY field cannot be null")
    @PositiveOrZero(message = "Salary must be positive or zero")
    private BigDecimal salary;

//    @Version // ZASTOSOWAC PESSIMISTICLOCKER
//    private Long version;
}

package com.example.exam.model.employee.position;

import com.example.exam.model.employee.Employee;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
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
    @Column(updatable = false)
    private Long id;
    private String name;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    private Employee employee;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal salary;


    //TODO
    // z kiedys
    //    @Version // ZASTOSOWAC PESSIMISTICLOCKER
    //    private Long version;
}

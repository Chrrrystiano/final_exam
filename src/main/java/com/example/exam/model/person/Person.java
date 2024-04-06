package com.example.exam.model.person;

import com.example.exam.model.employee.Employee;
import com.example.exam.model.pensioner.Pensioner;
import com.example.exam.model.student.Student;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "PERSONS")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    @Column(name = "type", insertable = false, updatable = false)
    private String type;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "The NAME field cannot be left empty")
    private String name;
    @NotBlank(message = "The SURNAME field cannot be left empty")
    private String surname;
    @Column(unique = true)
    @NotBlank(message = "The PESEL field cannot be left empty")
    @Pattern(regexp = "\\d{11}", message = "Pesel must have exactly 11 digits!")
    private String pesel;
    @NotNull(message = "The HEIGHT field cannot be left empty")
    @Min(value = 0, message = "Height must be greater than 0")
    @Max(value = 300, message = "Height must be less than 300")
    private double height;
    @NotNull(message = "The WEIGHT field cannot be left empty")
    @Min(value = 0, message = "Weight must be greater than 0")
    @Max(value = 300, message = "Weight must be less than 300")
    private double weight;
    @NotBlank(message = "The EMAIL field cannot be left empty")
    @Email(message = "Email must be valid")
    private String email;

    @Version
    private Long version;
}

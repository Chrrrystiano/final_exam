package com.example.exam.model.student.command;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStudentCommand {
    @NotBlank(message = "The NAME field cannot be left empty")
    private String name;
    @NotBlank(message = "The SURNAME field cannot be left empty")
    private String surname;
    @NotBlank(message = "The PESEL field cannot be left empty")
    @Pattern(regexp = "\\d{11}", message = "Pesel must have exactly 11 digits!")
    @Column(unique = true)
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
    @Column(unique = true)
    private String email;
    @NotBlank(message = "The UNIVERSITY field cannot be left empty")
    private String universityName;
    @NotNull(message = "The YEAR OF STUDY field cannot be null")
    @Min(value = 1, message = "Year of study must be at least 1")
    @Max(value = 5, message = "Year of study must be no more than 5")
    private int yearOfStudy;
    @NotBlank(message = "The FIELD OF STUDY field cannot be left empty")
    private String fieldOfStudy;
    @NotNull(message = "The SCHOLARSHIP AMOUNT field cannot be null")
    @PositiveOrZero(message = "Scholarship amount must be positive or zero")
    private BigDecimal scholarshipAmount;
}

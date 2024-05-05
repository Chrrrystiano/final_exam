package com.example.exam.model.pensioner.command;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePensionerCommand {
    private String type;
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
    @NotNull(message = "The PENSION AMOUNT field cannot be null")
    @PositiveOrZero(message = "Pension amount amount must be positive or zero")
    private BigDecimal pensionAmount;
    @NotNull(message = "The YEARS OF WORK field cannot be null")
    @Min(value = 1, message = "Year of work must be at least 1")
    private int yearsOfWork;
}

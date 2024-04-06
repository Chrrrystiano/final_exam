package com.example.exam.model.pensioner;

import com.example.exam.model.person.Person;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("PENSIONER")
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Pensioner extends Person {
    @NotNull(message = "The PENSION AMOUNT field cannot be null")
    @PositiveOrZero(message = "Pension amount amount must be positive or zero")
    @JsonProperty("pension_amount")
    private BigDecimal pensionAmount;
    @NotNull(message = "The YEARS OF WORK field cannot be null")
    @Min(value = 1, message = "Year of work must be at least 1")
    @JsonProperty("years_of_work")
    private int yearsOfWork;
}

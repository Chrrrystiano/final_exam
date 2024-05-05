package com.example.exam.model.pensioner;

import com.example.exam.model.person.Person;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
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
    @JsonProperty("pension_amount")
    private BigDecimal pensionAmount;
    @JsonProperty("years_of_work")
    private int yearsOfWork;
}
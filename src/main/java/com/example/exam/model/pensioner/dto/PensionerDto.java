package com.example.exam.model.pensioner.dto;

import com.example.exam.model.person.dto.PersonDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PensionerDto extends PersonDto {
    BigDecimal pensionAmount;
    int yearsOfWork;
}

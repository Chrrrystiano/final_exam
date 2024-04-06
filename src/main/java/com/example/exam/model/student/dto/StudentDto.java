package com.example.exam.model.student.dto;

import com.example.exam.model.person.dto.PersonDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDto extends PersonDto {
    private String universityName;
    private int yearOfStudy;
    private String fieldOfStudy;
    private BigDecimal scholarshipAmount;
}
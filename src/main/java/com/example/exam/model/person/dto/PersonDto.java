package com.example.exam.model.person.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PersonDto {
    String type;
    Long id;
    String name;
    String surname;
    String pesel;
    double height;
    double weight;
    String email;
}

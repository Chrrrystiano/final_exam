package com.example.exam.model.student;

import com.example.exam.model.person.Person;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("STUDENT")
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Student extends Person {
    @JsonProperty("university_name")
    private String universityName;
    @JsonProperty("year_of_study")
    private int yearOfStudy;
    @JsonProperty("field_of_study")
    private String fieldOfStudy;
    @JsonProperty("scholarship_amount")
    private BigDecimal scholarshipAmount;
}

















//package com.example.exam.model.student;
//
//import com.example.exam.model.person.Person;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import jakarta.persistence.DiscriminatorValue;
//import jakarta.persistence.Entity;
//import jakarta.validation.constraints.*;
//import lombok.*;
//import lombok.experimental.SuperBuilder;
//
//import java.math.BigDecimal;
//
//@Entity
//@DiscriminatorValue("STUDENT")
//@Data
//@SuperBuilder
//@EqualsAndHashCode(callSuper = true)
//@NoArgsConstructor
//@AllArgsConstructor
//public class Student extends Person {
//    @NotBlank(message = "The UNIVERSITY field cannot be left empty")
//    @JsonProperty("university_name")
//    private String universityName;
//    @NotNull(message = "The YEAR OF STUDY field cannot be null")
//    @Min(value = 1, message = "Year of study must be at least 1")
//    @Max(value = 5, message = "Year of study must be no more than 5")
//    @JsonProperty("year_of_study")
//    private int yearOfStudy;
//    @NotBlank(message = "The FIELD OF STUDY field cannot be left empty")
//    @JsonProperty("field_of_study")
//    private String fieldOfStudy;
//    @NotNull(message = "The SCHOLARSHIP AMOUNT field cannot be null")
//    @PositiveOrZero(message = "Scholarship amount must be positive or zero")
//    @JsonProperty("scholarship_amount")
//    private BigDecimal scholarshipAmount;
//}
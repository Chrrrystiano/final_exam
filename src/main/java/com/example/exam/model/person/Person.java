package com.example.exam.model.person;

import jakarta.persistence.*;
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
    @Column(updatable = false)
    private Long id;
    private String name;
    private String surname;
    @Column(unique = true)
    private String pesel;
    private double height;
    private double weight;
    @Column(unique = true)

    private String email;

    @Version
    private Long version = 0L;
}
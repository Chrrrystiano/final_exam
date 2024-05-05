package com.example.exam.service;

import com.example.exam.model.person.Person;
import com.example.exam.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;

    @Transactional
    public Person savePerson(Person person) {
        return personRepository.save(person);
    }

}

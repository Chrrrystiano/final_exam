package com.example.exam.service;

import com.example.exam.exceptions.EmailValidationException;
import com.example.exam.exceptions.PeselValidationException;
import com.example.exam.model.person.Person;
import com.example.exam.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PersonService {
    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Transactional
    public Person savePerson(Person person) {
        List<String> existingPesels = personRepository.findPesel(person.getPesel());
        if (!existingPesels.isEmpty()) {
            throw new PeselValidationException("Wrong PESEL number. This PESEL is already in the database.");
        }
        List<String> existingEmails = personRepository.findEmail(person.getEmail());
        if (!existingEmails.isEmpty()) {
            throw new EmailValidationException("Wrong EMAIL number. This EMAIL is already in the database.");
        }
        return personRepository.save(person);
    }

}

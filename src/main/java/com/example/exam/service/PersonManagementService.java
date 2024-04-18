package com.example.exam.service;

import com.example.exam.exceptions.PersonNotFoundException;
import com.example.exam.exceptions.UnsupportedPersonTypeException;
import com.example.exam.model.GenericPersonDto;
import com.example.exam.model.person.Person;
import com.example.exam.repository.PersonRepository;
import com.example.exam.strategies.person.PersonCreationStrategy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class PersonManagementService {
    private final List<PersonCreationStrategy<?>> strategyList;

    private final PersonRepository personRepository;

    @Autowired
    public PersonManagementService(PersonRepository personRepository,List<PersonCreationStrategy<?>> strategyList) {
        this.personRepository = personRepository;
        this.strategyList = strategyList;
    }

    public Person processPerson(GenericPersonDto genericPersonDto) throws JsonProcessingException {

        PersonCreationStrategy<?> strategy = strategyList.stream()
                .filter(s -> s.isApplicable(genericPersonDto.getType()))
                .findFirst()
                .orElseThrow(() -> new UnsupportedPersonTypeException("Unsupported person type: " + genericPersonDto.getType()));
        return strategy.createPerson(genericPersonDto.getData());
    }

    @Transactional
    public Person editPerson(Long id, GenericPersonDto genericPersonDto) {
        Person existingPerson = personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException("Person not found with ID: " + id));

        JsonNode personData = genericPersonDto.getData();

        PersonCreationStrategy<?> strategy = strategyList.stream()
                .filter(s -> s.supports(String.valueOf(existingPerson.getType())))
                .findFirst()
                .orElseThrow(() -> new UnsupportedPersonTypeException("Unsupported person type: " + existingPerson.getType()));

        return strategy.update(existingPerson, personData);
    }

}


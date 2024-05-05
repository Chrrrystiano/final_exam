package com.example.exam.service;

import com.example.exam.enums.PersonType;
import com.example.exam.exceptions.PersonNotFoundException;
import com.example.exam.exceptions.UnsupportedPersonTypeException;
import com.example.exam.model.GenericPersonDto;
import com.example.exam.model.person.Person;
import com.example.exam.model.person.command.CreatePersonCommand;
import com.example.exam.repository.PersonRepository;
import com.example.exam.strategies.person.PersonCreationStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonManagementService {
    private final List<PersonCreationStrategy<?>> strategyList;

    private final PersonRepository personRepository;

    public Person processPerson(CreatePersonCommand createPersonCommand) {
        PersonCreationStrategy<?> strategy = strategyList.stream()
                .filter(s -> s.isApplicable(PersonType.valueOf(createPersonCommand.getType())))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Unsupported person type: " + createPersonCommand.getType()));

        return strategy.createPerson(createPersonCommand);
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


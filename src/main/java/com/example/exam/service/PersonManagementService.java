package com.example.exam.service;

import com.example.exam.enums.PersonType;
import com.example.exam.exceptions.UnsupportedPersonTypeException;
import com.example.exam.model.GenericPersonDto;
import com.example.exam.model.person.Person;
import com.example.exam.strategies.PersonCreationStrategy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class PersonManagementService {
    private final List<PersonCreationStrategy<?>> creationStrategies;
    private final List<PersonCreationStrategy> editStrategies;

    @Autowired
    public PersonManagementService(List<PersonCreationStrategy<?>> creationStrategies, List<PersonCreationStrategy> editStrategies) {
        this.creationStrategies = creationStrategies;
        this.editStrategies = editStrategies;
    }

    public Person processPerson(GenericPersonDto genericPersonDto) throws JsonProcessingException {
        PersonCreationStrategy<?> strategy = creationStrategies.stream()
                .filter(s -> s.isApplicable(genericPersonDto.getType()))
                .findFirst()
                .orElseThrow(() -> new UnsupportedPersonTypeException("Unsupported person type: " + genericPersonDto.getType()));
        return strategy.createPerson(genericPersonDto.getData());
    }

    @Transactional
    public Person editPerson(GenericPersonDto genericPersonDto) {

        PersonType type = genericPersonDto.getType();
        JsonNode personData = genericPersonDto.getData();

        PersonCreationStrategy<?> strategy = editStrategies.stream()
                .filter(s -> s.supports(String.valueOf(type)))
                .findFirst()
                .orElseThrow(() -> new UnsupportedPersonTypeException("Unsupported person type: " + genericPersonDto.getType()));

        return strategy.update(personData);
    }

}


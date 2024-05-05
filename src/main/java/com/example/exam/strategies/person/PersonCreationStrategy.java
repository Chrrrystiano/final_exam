package com.example.exam.strategies.person;

import com.example.exam.enums.PersonType;
import com.example.exam.model.person.Person;
import com.example.exam.model.person.command.CreatePersonCommand;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

public interface PersonCreationStrategy<T extends CreatePersonCommand> {
    boolean isApplicable(PersonType type);

    Person createPerson(CreatePersonCommand createPersonCommand);

    Person update(Person existingPerson, JsonNode jsonNode);

    void save(Map<String, Object> recordMap);

    boolean supports(String type);
}

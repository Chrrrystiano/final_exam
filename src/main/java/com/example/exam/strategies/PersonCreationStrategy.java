package com.example.exam.strategies;

import com.example.exam.enums.PersonType;
import com.example.exam.model.person.Person;
import com.example.exam.model.person.dto.PersonDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

public interface PersonCreationStrategy<T> {
    boolean isApplicable(PersonType type);

    Person createPerson(JsonNode personDto) throws JsonProcessingException;

    void save(Map<String, Object> recordMap);

    boolean supports(String type);

    Person update(JsonNode jsonNode);
}

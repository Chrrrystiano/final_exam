package com.example.exam.model;

import com.example.exam.enums.PersonType;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenericPersonDto {
    private PersonType type;
    private JsonNode data;
}

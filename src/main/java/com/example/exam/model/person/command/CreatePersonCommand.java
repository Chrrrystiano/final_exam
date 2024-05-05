package com.example.exam.model.person.command;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class CreatePersonCommand {
    String type;
    Map<String, Object> parameters;
}

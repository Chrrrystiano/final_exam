package com.example.exam.model.person.command;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class UpdatePersonCommand {
    Map<String, Object> parameters;
    Long version;
}

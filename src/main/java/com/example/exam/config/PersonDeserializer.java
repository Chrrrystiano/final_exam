package com.example.exam.config;

import com.example.exam.model.person.Person;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Map;

public class PersonDeserializer extends StdDeserializer<Person> {

    private Map<String, Class<? extends Person>> typeMapping;

    public PersonDeserializer(Map<String, Class<? extends Person>> typeMapping) {
        super(Person.class);
        this.typeMapping = typeMapping;
    }
    @Override
    public Person deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode tree = jsonParser.getCodec().readTree(jsonParser);
        String typeIdentifier = tree.get("type").asText();
        Class<? extends Person> personClass = typeMapping.get(typeIdentifier.toLowerCase());
        if (personClass != null) {
            return mapper.treeToValue(tree, personClass);
        }
        throw new JsonParseException(jsonParser, "Unknown type: " + typeIdentifier);
    }
}

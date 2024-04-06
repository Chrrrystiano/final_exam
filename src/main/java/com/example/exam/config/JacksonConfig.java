package com.example.exam.config;

import com.example.exam.model.person.Person;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.reflections.Reflections;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper;
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer(Map<String, Class<? extends Person>> typeMapping) {
        return builder -> {
            SimpleModule module = new SimpleModule();
            module.addDeserializer(Person.class, new PersonDeserializer(typeMapping));
            builder.modulesToInstall(module);
        };
    }

    @Bean
    public Map<String, Class<? extends Person>> typeMapping() {
        Map<String, Class<? extends Person>> typeMapping = new HashMap<>();
        Reflections reflections = new Reflections("com.example.exam.model");

        Set<Class<? extends Person>> subTypes = reflections.getSubTypesOf(Person.class);

        for (Class<? extends Person> subType : subTypes) {
            String key = subType.getSimpleName().toLowerCase();
            typeMapping.put(key, subType);
        }
        return typeMapping;
    }
}

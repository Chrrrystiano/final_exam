package com.example.exam.controller;

import com.example.exam.model.person.command.CreatePersonCommand;
import com.example.exam.model.GenericPersonDto;
import com.example.exam.model.person.Person;
import com.example.exam.model.person.dto.PersonDto;
import com.example.exam.service.PersonSearchService;
import com.example.exam.service.PersonManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/people")
@RequiredArgsConstructor
public class PersonController {
    private final PersonManagementService personManagementService;
    private final PersonSearchService personSearchService;
    private final ModelMapper modelMapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Person> processPerson(@Valid @RequestBody CreatePersonCommand createPersonCommand) {
        Person createdPerson = personManagementService.processPerson(createPersonCommand);
        return new ResponseEntity<>(createdPerson, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> editPerson(@PathVariable("id") long id, @RequestBody @Valid GenericPersonDto genericPersonDto) {
        Person createdPerson = personManagementService.editPerson(id, genericPersonDto);
        return ResponseEntity.ok(modelMapper.map(createdPerson, PersonDto.class));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Page<Person>> searchPeople(
            @RequestParam(required = false) String type,
            @RequestParam Map<String, String> allParams,
            Pageable pageable) {

        Page<Person> dtos = personSearchService.searchPeopleWithCriteria(type, allParams, pageable);
        return ResponseEntity.ok(dtos);
    }
}

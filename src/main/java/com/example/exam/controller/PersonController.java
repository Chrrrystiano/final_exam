package com.example.exam.controller;

import com.example.exam.upload.service.ImportFileService;
import com.example.exam.upload.status.ImportResponse;
import com.example.exam.model.GenericPersonDto;
import com.example.exam.model.person.Person;
import com.example.exam.model.person.dto.PersonDto;
import com.example.exam.service.PersonSearchService;
import com.example.exam.service.PersonManagementService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

import static org.springframework.http.HttpStatus.OK;


@RestController
@RequestMapping("/api/people")
public class PersonController {
    private final PersonManagementService personManagementService;
    private final PersonSearchService personSearchService;
    private final ModelMapper modelMapper;
    private final ImportFileService importFileService;


    @Autowired
    public PersonController(ImportFileService importFileService, PersonManagementService personManagementService, PersonSearchService personSearchService, ModelMapper modelMapper) {
        this.personManagementService = personManagementService;
        this.personSearchService = personSearchService;
        this.modelMapper = modelMapper;
        this.importFileService = importFileService;
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> processPerson(@Valid @RequestBody GenericPersonDto genericPersonDto) throws JsonProcessingException {
        Person createdPerson = personManagementService.processPerson(genericPersonDto);
        return new ResponseEntity<>(createdPerson, HttpStatus.CREATED);
    }

    @PutMapping("/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> editPerson(@RequestBody GenericPersonDto genericPersonDto) {
        Person createdPerson = personManagementService.editPerson(genericPersonDto);
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

    @PostMapping("/import")
    @PreAuthorize("hasRole('ADMIN') or hasRole('IMPORTER')")
    public ResponseEntity<ImportResponse> importFile(@RequestParam("file") MultipartFile file) {
        String taskId = UUID.randomUUID().toString();
        ImportResponse response = new ImportResponse("The import has been accepted", taskId);
        importFileService.importFile(file, taskId);
        return new ResponseEntity<>(response, OK);
    }

}

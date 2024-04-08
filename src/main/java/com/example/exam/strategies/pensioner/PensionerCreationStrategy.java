package com.example.exam.strategies.pensioner;

import com.example.exam.enums.PersonType;
import com.example.exam.exceptions.EmailValidationException;
import com.example.exam.exceptions.PeselValidationException;
import com.example.exam.model.pensioner.Pensioner;
import com.example.exam.model.pensioner.dto.PensionerDto;
import com.example.exam.model.person.Person;
import com.example.exam.repository.PensionerRepository;
import com.example.exam.repository.PersonRepository;
import com.example.exam.service.PensionerService;
import com.example.exam.strategies.person.PersonCreationStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
public class PensionerCreationStrategy implements PersonCreationStrategy<JsonNode> {

    private final PensionerRepository pensionerRepository;
    private final PersonRepository personRepository;
    private final ObjectMapper objectMapper;
    private final PensionerService pensionerService;

    @Autowired
    public PensionerCreationStrategy(PensionerRepository pensionerRepository, ObjectMapper objectMapper, PersonRepository personRepository, PensionerService pensionerService) {
        this.pensionerRepository = pensionerRepository;
        this.objectMapper = objectMapper;
        this.personRepository = personRepository;
        this.pensionerService = pensionerService;
    }

    @Override
    public boolean isApplicable(PersonType type) {
        return PersonType.PENSIONER.equals(type);
    }

    @Override
    @Transactional
    public Person createPerson(JsonNode jsonNode) {
        PensionerDto pensionerDto = objectMapper.convertValue(jsonNode, PensionerDto.class);
        if (personRepository.existingPesel(pensionerDto.getPesel())) {
            throw new PeselValidationException("Wrong PESEL number. This PESEL is already in the database.");
        }
        if (personRepository.existingEmail(pensionerDto.getEmail())) {
            throw new EmailValidationException("Wrong EMAIL number. This EMAIL is already in the database.");
        }
        Pensioner pensioner = convertToEntity(pensionerDto);
        pensionerRepository.save(pensioner);
        return pensioner;
    }

    @Override
    public Person update(JsonNode jsonNode) {
        PensionerDto pensionerDto = objectMapper.convertValue(jsonNode, PensionerDto.class);
        Pensioner pensioner = convertToEntity(pensionerDto);
        pensionerRepository.save(pensioner);
        return pensioner;
    }

    private Pensioner convertToEntity(PensionerDto pensionerDto) {
        return Pensioner.builder()
                .name(pensionerDto.getName())
                .surname(pensionerDto.getSurname())
                .pesel(pensionerDto.getPesel())
                .height(pensionerDto.getHeight())
                .weight(pensionerDto.getWeight())
                .email(pensionerDto.getEmail())
                .pensionAmount(pensionerDto.getPensionAmount())
                .yearsOfWork((pensionerDto.getYearsOfWork()))
                .build();
    }

    @Override
    public void save(Map<String, Object> recordMap) {
        Pensioner pensioner = objectMapper.convertValue(recordMap, Pensioner.class);
        pensionerService.savePensioner(pensioner);
    }

    @Override
    public boolean supports(String type) {
        return "PENSIONER".equals(type);
    }
}

package com.example.exam.strategies.pensioner;

import com.example.exam.enums.PersonType;
import com.example.exam.exceptions.FailedValidationException;
import com.example.exam.model.pensioner.Pensioner;
import com.example.exam.model.pensioner.command.CreatePensionerCommand;
import com.example.exam.model.pensioner.dto.PensionerDto;
import com.example.exam.model.person.Person;
import com.example.exam.model.person.command.CreatePersonCommand;
import com.example.exam.repository.PensionerRepository;
import com.example.exam.service.PensionerService;
import com.example.exam.strategies.person.PersonCreationStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;


@Component
@RequiredArgsConstructor
public class PensionerCreationStrategy implements PersonCreationStrategy<CreatePersonCommand> {

    private final PensionerRepository pensionerRepository;
    private final ObjectMapper objectMapper;
    private final PensionerService pensionerService;

    @Override
    public boolean isApplicable(PersonType type) {
        return PersonType.PENSIONER.equals(type);
    }

    @Override
    @Transactional
    public Person createPerson(CreatePersonCommand createPersonCommand) {
        CreatePensionerCommand pensionerCommand = objectMapper.convertValue(createPersonCommand.getParameters(), CreatePensionerCommand.class);
        validateParameters(pensionerCommand);
        Pensioner pensioner = convertToEntity(pensionerCommand);
        pensionerRepository.save(pensioner);
        return pensioner;
    }

    private static void validateParameters(CreatePensionerCommand pensionerCommand) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<CreatePensionerCommand>> violations = validator.validate(pensionerCommand);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<CreatePensionerCommand> violation : violations) {
                sb.append(violation.getMessage()).append("; ");
            }
            throw new FailedValidationException("Validation failed: " + sb.toString());
        }
    }

    @Override
    public Person update(Person existingPerson, JsonNode jsonNode) {
        Pensioner existingPensioner = (Pensioner) existingPerson;
        PensionerDto pensionerDto = objectMapper.convertValue(jsonNode, PensionerDto.class);
        editExistingPensionerWithNewData(existingPensioner, pensionerDto);
        pensionerRepository.save(existingPensioner);
        return existingPensioner;
    }

    private void editExistingPensionerWithNewData(Pensioner existingPensioner, PensionerDto pensionerDto) {
        existingPensioner.setName(pensionerDto.getName());
        existingPensioner.setSurname(pensionerDto.getSurname());
        existingPensioner.setHeight(pensionerDto.getHeight());
        existingPensioner.setWeight(pensionerDto.getWeight());
        existingPensioner.setPensionAmount(pensionerDto.getPensionAmount());
        existingPensioner.setYearsOfWork(pensionerDto.getYearsOfWork());
    }


    private Pensioner convertToEntity(CreatePensionerCommand createPensionerCommand) {
        return Pensioner.builder()
                .name(createPensionerCommand.getName())
                .surname(createPensionerCommand.getSurname())
                .pesel(createPensionerCommand.getPesel())
                .height(createPensionerCommand.getHeight())
                .weight(createPensionerCommand.getWeight())
                .email(createPensionerCommand.getEmail())
                .pensionAmount(createPensionerCommand.getPensionAmount())
                .yearsOfWork((createPensionerCommand.getYearsOfWork()))
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

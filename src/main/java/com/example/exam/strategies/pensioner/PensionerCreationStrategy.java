package com.example.exam.strategies.pensioner;

import com.example.exam.enums.PersonType;
import com.example.exam.exceptions.FailedValidationException;
import com.example.exam.model.pensioner.Pensioner;
import com.example.exam.model.pensioner.command.CreatePensionerCommand;
import com.example.exam.model.pensioner.command.UpdatePensionerCommand;
import com.example.exam.model.person.Person;
import com.example.exam.model.person.command.CreatePersonCommand;
import com.example.exam.model.person.command.UpdatePersonCommand;
import com.example.exam.repository.PensionerRepository;
import com.example.exam.service.PensionerService;
import com.example.exam.strategies.person.PersonCreationStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;

import java.util.Map;
import java.util.Set;


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
        validateCreateParameters(pensionerCommand);
        Pensioner pensioner = convertToEntity(pensionerCommand);
        pensionerRepository.save(pensioner);
        return pensioner;
    }

    @Override
    @Transactional
    public Person updatePerson(Person existingPerson, UpdatePersonCommand updatePersonCommand) {
        Pensioner pensioner = (Pensioner) existingPerson;
        Map<String, Object> parameters = updatePersonCommand.getParameters();
        UpdatePensionerCommand updatePensionerCommand = objectMapper.convertValue(parameters, UpdatePensionerCommand.class);
        validateUpdateParameters(updatePensionerCommand);

        pensioner.setName(updatePensionerCommand.getName());
        pensioner.setSurname(updatePensionerCommand.getSurname());
        pensioner.setPesel(updatePensionerCommand.getPesel());
        pensioner.setHeight(updatePensionerCommand.getHeight());
        pensioner.setWeight(updatePensionerCommand.getWeight());
        pensioner.setEmail(updatePensionerCommand.getEmail());
        pensioner.setPensionAmount(updatePensionerCommand.getPensionAmount());
        pensioner.setYearsOfWork(updatePensionerCommand.getYearsOfWork());

        pensionerRepository.save(pensioner);

        return pensioner;
    }

    private static void validateCreateParameters(CreatePensionerCommand pensionerCommand) {
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

    private static void validateUpdateParameters(UpdatePensionerCommand updatePensionerCommand) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<UpdatePensionerCommand>> violations = validator.validate(updatePensionerCommand);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<UpdatePensionerCommand> violation : violations) {
                sb.append(violation.getMessage()).append("; ");
            }
            throw new FailedValidationException("Validation failed: " + sb.toString());
        }
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

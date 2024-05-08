package com.example.exam.strategies.employee;

import com.example.exam.enums.PersonType;
import com.example.exam.exceptions.FailedValidationException;
import com.example.exam.model.employee.Employee;
import com.example.exam.model.employee.command.CreateEmployeeCommand;
import com.example.exam.model.employee.command.UpdateEmployeeCommand;
import com.example.exam.model.employee.position.Position;
import com.example.exam.model.employee.position.command.CreatePositionCommand;
import com.example.exam.model.person.Person;
import com.example.exam.model.person.command.CreatePersonCommand;
import com.example.exam.model.person.command.UpdatePersonCommand;
import com.example.exam.repository.EmployeeRepository;
import com.example.exam.repository.PositionRepository;
import com.example.exam.service.EmployeeService;
import com.example.exam.strategies.person.PersonCreationStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class EmployeeCreationStrategy implements PersonCreationStrategy<CreatePersonCommand> {

    private final EmployeeRepository employeeRepository;
    private final PositionRepository positionRepository;
    private final EmployeeService employeeService;
    private final ObjectMapper objectMapper;

    @Override
    public boolean isApplicable(PersonType type) {
        return PersonType.EMPLOYEE.equals(type);
    }


    @Override
    @Transactional
    public Person createPerson(CreatePersonCommand createPersonCommand) {
        CreateEmployeeCommand employeeCommand = objectMapper.convertValue(createPersonCommand.getParameters(), CreateEmployeeCommand.class);
        validateParameters(employeeCommand);
        Employee employee = convertToEntity(employeeCommand);
        employeeRepository.save(employee);
        if (createPersonCommand.getParameters().containsKey("positions")) {
            List<Map<String, Object>> positionsData = (List<Map<String, Object>>) createPersonCommand.getParameters().get("positions");
            for (Map<String, Object> positionData : positionsData) {
                CreatePositionCommand createPositionCommand = objectMapper.convertValue(positionData, CreatePositionCommand.class);
                validatePositionParameters(createPositionCommand);
                Position position = convertCreatePositionCommandToEntity(createPositionCommand, employee);
                positionRepository.save(position);
            }
        }
        employeeService.updateEmployeeCurrentPositionAndSalary(employee);
        return employee;
    }

    private static void validateParameters(CreateEmployeeCommand employeeCommand) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<CreateEmployeeCommand>> violations = validator.validate(employeeCommand);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<CreateEmployeeCommand> violation : violations) {
                sb.append(violation.getMessage()).append("; ");
            }
            throw new FailedValidationException("Validation failed: " + sb.toString());
        }
    }

    private static void validatePositionParameters(CreatePositionCommand createPositionCommand) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<CreatePositionCommand>> violations = validator.validate(createPositionCommand);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<CreatePositionCommand> violation : violations) {
                sb.append(violation.getMessage()).append("; ");
            }
            throw new FailedValidationException("Validation failed: " + sb.toString());
        }
    }

    @Override
    public Person updatePerson(Person existingPerson, UpdatePersonCommand updatePersonCommand) {
        Employee employee = (Employee) existingPerson;
        Map<String, Object> parameters = updatePersonCommand.getParameters();
        UpdateEmployeeCommand updateEmployeeCommand = objectMapper.convertValue(parameters, UpdateEmployeeCommand.class);
        validateUpdateParameters(updateEmployeeCommand);

        employee.setName(updateEmployeeCommand.getName());
        employee.setSurname(updateEmployeeCommand.getSurname());
        employee.setPesel(updateEmployeeCommand.getPesel());
        employee.setHeight(updateEmployeeCommand.getHeight());
        employee.setWeight(updateEmployeeCommand.getWeight());
        employee.setEmail(updateEmployeeCommand.getEmail());

        employeeRepository.save(employee);

        return employee;
    }

    private static void validateUpdateParameters(UpdateEmployeeCommand updateEmployeeCommand) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<UpdateEmployeeCommand>> violations = validator.validate(updateEmployeeCommand);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<UpdateEmployeeCommand> violation : violations) {
                sb.append(violation.getMessage()).append("; ");
            }
            throw new FailedValidationException("Validation failed: " + sb.toString());
        }
    }

    private Employee convertToEntity(CreateEmployeeCommand createEmployeeCommand) {
        return Employee.builder()
                .type("EMPLOYEE")
                .name(createEmployeeCommand.getName())
                .surname(createEmployeeCommand.getSurname())
                .pesel(createEmployeeCommand.getPesel())
                .height(createEmployeeCommand.getHeight())
                .weight(createEmployeeCommand.getWeight())
                .email(createEmployeeCommand.getEmail())
                .build();
    }

    private Position convertCreatePositionCommandToEntity(CreatePositionCommand createPositionCommand, Employee employee) {
        return Position.builder()
                .name(createPositionCommand.getName())
                .startDate(createPositionCommand.getStartDate())
                .endDate(createPositionCommand.getEndDate())
                .salary(createPositionCommand.getSalary())
                .employee(employee)
                .build();
    }

    @Override
    public void save(Map<String, Object> recordMap) {
        Employee employee = objectMapper.convertValue(recordMap, Employee.class);
        employeeService.saveEmployee(employee);
    }

    @Override
    public boolean supports(String type) {
        return "EMPLOYEE".equals(type);
    }

}
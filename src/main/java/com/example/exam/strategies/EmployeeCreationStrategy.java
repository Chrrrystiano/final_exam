package com.example.exam.strategies;

import com.example.exam.enums.PersonType;
import com.example.exam.exceptions.EmailValidationException;
import com.example.exam.exceptions.PeselValidationException;
import com.example.exam.model.employee.Employee;
import com.example.exam.model.employee.dto.EmployeeDto;
import com.example.exam.model.employee.position.Position;
import com.example.exam.model.employee.position.dto.PositionDto;
import com.example.exam.model.person.Person;
import com.example.exam.repository.EmployeeRepository;
import com.example.exam.repository.PersonRepository;
import com.example.exam.repository.PositionRepository;
import com.example.exam.service.EmployeeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
public class EmployeeCreationStrategy implements PersonCreationStrategy<JsonNode> {

    private final EmployeeRepository employeeRepository;
    private final PersonRepository personRepository;
    private final PositionRepository positionRepository;
    private final EmployeeService employeeService;
    private final ObjectMapper objectMapper;

    @Autowired
    public EmployeeCreationStrategy(EmployeeRepository employeeRepository, ObjectMapper objectMapper, PersonRepository personRepository, EmployeeService employeeService, PositionRepository positionRepository) {
        this.employeeRepository = employeeRepository;
        this.objectMapper = objectMapper;
        this.personRepository = personRepository;
        this.employeeService = employeeService;
        this.positionRepository = positionRepository;
    }

    @Override
    public boolean isApplicable(PersonType type) {
        return PersonType.EMPLOYEE.equals(type);
    }

    @Override
    @Transactional
    public Person createPerson(JsonNode jsonNode) throws JsonProcessingException {
        EmployeeDto employeeDto = objectMapper.convertValue(jsonNode, EmployeeDto.class);
        if (personRepository.existingPesel(employeeDto.getPesel())) {
            throw new PeselValidationException("Wrong PESEL number. This PESEL is already in the database.");
        }

        if (personRepository.existingEmail(employeeDto.getEmail())) {
            throw new EmailValidationException("Wrong PESEL number. This PESEL is already in the database.");
        }
        Employee employee = convertToEntity(employeeDto);
        employee = employeeRepository.save(employee);
        if (employeeDto.getPositions() != null) {
            for (PositionDto positionDto : employeeDto.getPositions()) {
                Position position = convertPositionDtoToEntity(positionDto, employee);
                positionRepository.save(position);
            }
        }
        employeeService.updateEmployeeCurrentPositionAndSalary(employee);
        return employee;
    }

    @Override
    public Person update(JsonNode jsonNode) {
        EmployeeDto employeeDto = objectMapper.convertValue(jsonNode, EmployeeDto.class);
        Employee employee = convertToEntity(employeeDto);
        employeeRepository.save(employee);
        return employee;
    }

    private Employee convertToEntity(EmployeeDto employeeDto) {
        return Employee.builder()
                .name(employeeDto.getName())
                .surname(employeeDto.getSurname())
                .pesel(employeeDto.getPesel())
                .height(employeeDto.getHeight())
                .weight(employeeDto.getWeight())
                .email(employeeDto.getEmail())
                .build();
    }

    private Position convertPositionDtoToEntity(PositionDto positionDto, Employee employee) {
        Position position = new Position();
        position.setName(positionDto.getName());
        position.setStartDate(positionDto.getStartDate());
        position.setEndDate(positionDto.getEndDate());
        position.setSalary(positionDto.getSalary());
        position.setEmployee(employee);
        return position;
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
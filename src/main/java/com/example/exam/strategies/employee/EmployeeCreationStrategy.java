package com.example.exam.strategies.employee;

import com.example.exam.enums.PersonType;
import com.example.exam.model.employee.Employee;
import com.example.exam.model.employee.dto.EmployeeDto;
import com.example.exam.model.employee.position.Position;
import com.example.exam.model.employee.position.dto.PositionDto;
import com.example.exam.model.person.Person;
import com.example.exam.repository.EmployeeRepository;
import com.example.exam.repository.PositionRepository;
import com.example.exam.service.EmployeeService;
import com.example.exam.strategies.person.PersonCreationStrategy;
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
    private final PositionRepository positionRepository;
    private final EmployeeService employeeService;
    private final ObjectMapper objectMapper;

    @Autowired
    public EmployeeCreationStrategy(EmployeeRepository employeeRepository, ObjectMapper objectMapper, EmployeeService employeeService, PositionRepository positionRepository) {
        this.employeeRepository = employeeRepository;
        this.objectMapper = objectMapper;
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
    public Person update(Person existingPerson, JsonNode jsonNode) {
        Employee existingEmployee = (Employee) existingPerson;
        EmployeeDto employeeDto = objectMapper.convertValue(jsonNode, EmployeeDto.class);
        editExistingEmployeeWithNewData(existingEmployee, employeeDto);
        employeeRepository.save(existingEmployee);
        return existingEmployee;
    }

    private void editExistingEmployeeWithNewData(Employee existingEmployee, EmployeeDto employeeDto) {
        existingEmployee.setName(employeeDto.getName());
        existingEmployee.setSurname(employeeDto.getSurname());
        existingEmployee.setHeight(employeeDto.getHeight());
        existingEmployee.setWeight(employeeDto.getWeight());
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
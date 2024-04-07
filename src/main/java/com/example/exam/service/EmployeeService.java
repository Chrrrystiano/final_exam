package com.example.exam.service;

import com.example.exam.exceptions.*;
import com.example.exam.model.employee.Employee;
import com.example.exam.model.employee.position.Position;
import com.example.exam.model.employee.position.dto.PositionDto;
import com.example.exam.repository.EmployeeRepository;
import com.example.exam.repository.PersonRepository;
import com.example.exam.repository.PositionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final PositionRepository positionRepository;
    private final PersonRepository personRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, PositionRepository positionRepository, PersonRepository personRepository) {
        this.employeeRepository = employeeRepository;
        this.positionRepository = positionRepository;
        this.personRepository = personRepository;
    }

    @Transactional
    public void addPositionToEmployee(Long employeeId, PositionDto newPositionDto) {
        Employee employee = employeeRepository.findWithLockById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));

        Position newPosition = convertPositionDtoToEntity(newPositionDto);
        newPosition.setEmployee(employee);
        validatePositionStartDate(newPosition, employeeId);
        positionRepository.save(newPosition);
        updateEmployeeCurrentPositionAndSalary(employee);
        employeeRepository.save(employee);
    }

    public Position getCurrentPosition(Long employeeId) {
        List<Position> positions = positionRepository.findByEmployeeId(employeeId);

        return positions.stream()
                .filter(position -> position.getEndDate() == null)
                .findFirst()
                .orElseThrow(() -> new NoCurrentPositionException("No current position found for employee ID: " + employeeId));
    }

    @Transactional
    public void updateEmployeeCurrentPositionAndSalary(Employee employee) {
        Position latestPosition = positionRepository.findByEmployeeId(employee.getId()).stream()
                .filter(p -> p.getEndDate() == null || p.getEndDate().isAfter(LocalDate.now()))
                .max(Comparator.comparing(Position::getStartDate))
                .orElse(null);


        if (latestPosition != null) {
            employee.setCurrentPosition(latestPosition.getName());
            employee.setCurrentPositionStartDate(latestPosition.getStartDate());
            employee.setCurrentSalary(latestPosition.getSalary());
        }
    }

    private Position convertPositionDtoToEntity(PositionDto positionDto) {
        return Position.builder()
                .name(positionDto.getName())
                .startDate(positionDto.getStartDate())
                .endDate(positionDto.getEndDate())
                .salary(positionDto.getSalary())
                .build();
    }

    private void validatePositionStartDate(Position position, Long employeeId) {
        LocalDate currentPositionDate = getCurrentPosition(employeeId).getStartDate();
        if (position.getStartDate().isAfter(LocalDate.now())) {
            throw new DateValidationException("Invalid date, you cannot enter a future date.");
        }
        if (position.getStartDate().isBefore(currentPositionDate)) {
            throw new DateValidationException("Incorrect date, you cannot provide a date older than the one in your current position.");
        }
    }

    @Transactional
    public void saveEmployee(Employee employee) {
        if (personRepository.existingPesel(employee.getPesel())) {
            throw new PeselValidationException("Wrong PESEL number. This PESEL is already in the database.");
        }

        if (personRepository.existingEmail(employee.getEmail())) {
            throw new EmailValidationException("Wrong EMAIL number. This EMAIL is already in the database.");
        }
        try {
            employeeRepository.save(employee);
        } catch (NumberFormatException e) {
            throw new InvalidDataFileException("Error processing record due to number format issue: " + e.getMessage());
        }
    }


}
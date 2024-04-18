package com.example.exam.service;

import com.example.exam.exceptions.*;
import com.example.exam.model.employee.Employee;
import com.example.exam.model.employee.position.Position;
import com.example.exam.model.employee.position.dto.PositionDto;
import com.example.exam.repository.EmployeeRepository;
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

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, PositionRepository positionRepository) {
        this.employeeRepository = employeeRepository;
        this.positionRepository = positionRepository;
    }

    @Transactional
    public void addPositionToEmployee(Long employeeId, PositionDto newPositionDto) {
        Employee employee = employeeRepository.findWithLockById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));

        Position newPosition = convertPositionDtoToEntity(newPositionDto);
        Position currentPosition = null;

        try {
            currentPosition = getCurrentPosition(employeeId);
        } catch (NoCurrentPositionException e) {
            validationOfPositionDates(newPosition, employee.getPositions());
        }
        if (currentPosition != null && currentPosition.getEndDate() == null && newPosition.getStartDate().isAfter(currentPosition.getStartDate())) {
            currentPosition.setEndDate(newPosition.getStartDate().minusDays(1));
        } else if (currentPosition != null) {
            validationOfPositionDates(newPosition, employee.getPositions());
        }

        newPosition.setEmployee(employee);
        positionRepository.save(newPosition);
        updateEmployeeCurrentPositionAndSalary(employee);
        employeeRepository.save(employee);
    }

    public Position getCurrentPosition(Long employeeId) {
        return positionRepository.findCurrentPositionByEmployeeId(employeeId)
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
        } else {
            employee.setCurrentPosition(null);
            employee.setCurrentPositionStartDate(null);
            employee.setCurrentSalary(null);
        }
    }

    private void validationOfPositionDates(Position position, List<Position> positionList) {
        for (Position existingPosition : positionList) {
            if (checkingTheWorkingPeriod(position.getStartDate(), position.getEndDate(), existingPosition.getStartDate(), existingPosition.getEndDate())) {
                throw new DateValidationException("Incorrect date. The new date range: " + position.getStartDate() + " - " + position.getEndDate() +
                        " coincides with the existing range from: " + existingPosition.getStartDate() + " - " + existingPosition.getEndDate());
            }
        }
    }

    private boolean checkingTheWorkingPeriod(LocalDate newPositionStartDate, LocalDate newPositionEndDate, LocalDate savedPositionStartDate, LocalDate savedPositionEndDate) {
        return !newPositionStartDate.isAfter(savedPositionEndDate) && !newPositionEndDate.isBefore(savedPositionStartDate);
    }

    private Position convertPositionDtoToEntity(PositionDto positionDto) {
        return Position.builder()
                .name(positionDto.getName())
                .startDate(positionDto.getStartDate())
                .endDate(positionDto.getEndDate())
                .salary(positionDto.getSalary())
                .build();
    }

    @Transactional
    public void saveEmployee(Employee employee) {
        try {
            employeeRepository.save(employee);
        } catch (NumberFormatException e) {
            throw new InvalidDataFileException("Error processing record due to number format issue: " + e.getMessage());
        }
    }
}


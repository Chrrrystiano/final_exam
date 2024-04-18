package com.example.exam.controller;

import com.example.exam.model.employee.position.dto.PositionDto;
import com.example.exam.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/{id}/update-position")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignNewPosition(@PathVariable("id") Long id, @RequestBody @Valid PositionDto positionDto) {
        employeeService.addPositionToEmployee(id, positionDto);
        return ResponseEntity.ok().build();
    }


}

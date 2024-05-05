package com.example.exam.controller;

import com.example.exam.model.employee.position.command.CreatePositionCommand;
import com.example.exam.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping("/{id}/update-position")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignNewPosition(@PathVariable("id") Long id, @RequestBody @Valid CreatePositionCommand createPositionCommand) {
        employeeService.addPositionToEmployee(id, createPositionCommand);
        return ResponseEntity.ok().build();
    }
}

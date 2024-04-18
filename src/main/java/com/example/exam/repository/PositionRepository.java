package com.example.exam.repository;

import com.example.exam.model.employee.position.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PositionRepository extends JpaRepository<Position, Long> {
    List<Position> findByEmployeeId(Long employeeId);
    @Query("SELECT p FROM Position p WHERE p.employee.id = :employeeId AND p.startDate <= CURRENT_DATE AND (p.endDate IS NULL OR p.endDate > CURRENT_DATE)")
    Optional<Position> findCurrentPositionByEmployeeId(@Param("employeeId") Long employeeId);
}

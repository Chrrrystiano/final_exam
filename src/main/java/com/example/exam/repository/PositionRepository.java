package com.example.exam.repository;

import com.example.exam.model.employee.position.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
    List<Position> findByEmployeeId(Long employeeId);

    @Query("SELECT p FROM Position p WHERE p.employee.id = :employeeId ORDER BY p.startDate DESC")
    Optional<Position> findByEmployeeIdAndEndDateIsNull(@Param("employeeId") Long employeeId);
}

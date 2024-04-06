package com.example.exam.jdbc.status;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImportFileStatusRepository extends JpaRepository<ImportFileStatus, String> {
    Optional<ImportFileStatus> findByTaskId(String taskId);
}

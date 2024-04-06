package com.example.exam.repository;

import com.example.exam.model.security.permission.EPermission;
import com.example.exam.model.security.permission.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(EPermission name);
}

package com.example.exam.repository;

import com.example.exam.model.pensioner.Pensioner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PensionerRepository extends JpaRepository<Pensioner, Long> {
}

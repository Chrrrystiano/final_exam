package com.example.exam.repository;

import com.example.exam.model.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long>, QuerydslPredicateExecutor<Person> {
    @Query("SELECT COUNT(p) > 0 FROM Person p WHERE p.pesel = :pesel")
    boolean existingPesel(@Param("pesel") String pesel);

    @Query("SELECT p.pesel FROM Person p WHERE p.pesel = :pesel")
    List<String> findPesel(@Param("pesel") String pesel);

    @Query("SELECT COUNT(p) > 0 FROM Person p WHERE p.email = :email")
    boolean existingEmail(@Param("email") String email);

    @Query("SELECT p.email FROM Person p WHERE p.email = :email")
    List<String> findEmail(@Param("email") String email);

}

package com.example.exam.service;

import com.example.exam.exceptions.EmailValidationException;
import com.example.exam.exceptions.InvalidDataFileException;
import com.example.exam.exceptions.PeselValidationException;
import com.example.exam.model.student.Student;
import com.example.exam.repository.PersonRepository;
import com.example.exam.repository.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final PersonRepository personRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository, PersonRepository personRepository) {
        this.studentRepository = studentRepository;
        this.personRepository = personRepository;
    }

    @Transactional
    public void saveStudent(Student student) {
        if (personRepository.existingPesel(student.getPesel())) {
            throw new PeselValidationException("Wrong PESEL number. This PESEL is already in the database.");
        }

        if (personRepository.existingEmail(student.getEmail())) {
            throw new EmailValidationException("Wrong PESEL number. This PESEL is already in the database.");
        }
        try {
            studentRepository.save(student);
        } catch (NumberFormatException e) {
            throw new InvalidDataFileException("Error processing record due to number format issue: " + e.getMessage());
        }
    }

}

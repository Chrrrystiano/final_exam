package com.example.exam.service;

import com.example.exam.exceptions.InvalidDataFileException;
import com.example.exam.model.student.Student;
import com.example.exam.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;

    @Transactional
    public void saveStudent(Student student) {
        try {
            studentRepository.save(student);
        } catch (NumberFormatException e) {
            throw new InvalidDataFileException("Error processing record due to number format issue: " + e.getMessage());
        }
    }

}

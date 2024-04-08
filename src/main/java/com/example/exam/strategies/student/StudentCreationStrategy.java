package com.example.exam.strategies.student;

import com.example.exam.enums.PersonType;
import com.example.exam.exceptions.EmailValidationException;
import com.example.exam.exceptions.PeselValidationException;
import com.example.exam.model.person.Person;
import com.example.exam.model.student.Student;
import com.example.exam.model.student.dto.StudentDto;
import com.example.exam.repository.PersonRepository;
import com.example.exam.repository.StudentRepository;
import com.example.exam.service.StudentService;
import com.example.exam.strategies.person.PersonCreationStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
public class StudentCreationStrategy implements PersonCreationStrategy<JsonNode> {

    private final StudentRepository studentRepository;
    private final PersonRepository personRepository;
    private final StudentService studentService;
    private final ObjectMapper objectMapper;

    @Autowired
    public StudentCreationStrategy(StudentRepository studentRepository, ObjectMapper objectMapper, PersonRepository personRepository, StudentService studentService) {
        this.studentRepository = studentRepository;
        this.objectMapper = objectMapper;
        this.personRepository = personRepository;
        this.studentService = studentService;
    }

    @Override
    @Transactional
    public Person createPerson(JsonNode jsonNode) {
        StudentDto studentDto = objectMapper.convertValue(jsonNode, StudentDto.class);
        if (personRepository.existingPesel(studentDto.getPesel())) {
            throw new PeselValidationException("Wrong PESEL number. This PESEL is already in the database.");
        }
        if (personRepository.existingEmail(studentDto.getEmail())) {
            throw new EmailValidationException("Wrong EMAIL number. This EMAIL is already in the database.");
        }
        Student student = convertToEntity(studentDto);
        studentRepository.save(student);
        return student;
    }

    @Override
    public Person update(JsonNode jsonNode) {
        StudentDto studentDto = objectMapper.convertValue(jsonNode, StudentDto.class);
        Student student = convertToEntity(studentDto);
        studentRepository.save(student);
        return student;
    }

    private Student convertToEntity(StudentDto studentDto) {
        return Student.builder()
                .name(studentDto.getName())
                .surname(studentDto.getSurname())
                .pesel(studentDto.getPesel())
                .height(studentDto.getHeight())
                .weight(studentDto.getWeight())
                .email(studentDto.getEmail())
                .universityName(studentDto.getUniversityName())
                .yearOfStudy(studentDto.getYearOfStudy())
                .fieldOfStudy(studentDto.getFieldOfStudy())
                .scholarshipAmount(studentDto.getScholarshipAmount())
                .build();
    }

    @Override
    public void save(Map<String, Object> recordMap) {
        Student student = objectMapper.convertValue(recordMap, Student.class);
        studentService.saveStudent(student);
    }

    @Override
    public boolean supports(String type) {
        return "STUDENT".equals(type);
    }

    @Override
    public boolean isApplicable(PersonType type) {
        return PersonType.STUDENT.equals(type);
    }
}
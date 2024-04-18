package com.example.exam.strategies.student;

import com.example.exam.enums.PersonType;
import com.example.exam.model.person.Person;
import com.example.exam.model.student.Student;
import com.example.exam.model.student.dto.StudentDto;
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
    private final StudentService studentService;
    private final ObjectMapper objectMapper;

    @Autowired
    public StudentCreationStrategy(StudentRepository studentRepository, ObjectMapper objectMapper, StudentService studentService) {
        this.studentRepository = studentRepository;
        this.objectMapper = objectMapper;
        this.studentService = studentService;
    }

    @Override
    @Transactional
    public Person createPerson(JsonNode jsonNode) {
        StudentDto studentDto = objectMapper.convertValue(jsonNode, StudentDto.class);
        Student student = convertToEntity(studentDto);
        studentRepository.save(student);
        return student;
    }

    @Override
    public Person update(Person existingPerson, JsonNode jsonNode) {
        Student existingStudent = (Student) existingPerson;
        StudentDto studentDto = objectMapper.convertValue(jsonNode, StudentDto.class);
        editExistingStudentWithNewData(existingStudent, studentDto);
        studentRepository.save(existingStudent);
        return existingStudent;
    }

    private void editExistingStudentWithNewData(Student existingStudent, StudentDto studentDto) {
        existingStudent.setName(studentDto.getName());
        existingStudent.setSurname(studentDto.getSurname());
        existingStudent.setHeight(studentDto.getHeight());
        existingStudent.setWeight(studentDto.getWeight());
        existingStudent.setUniversityName(studentDto.getUniversityName());
        existingStudent.setYearOfStudy(studentDto.getYearOfStudy());
        existingStudent.setFieldOfStudy(studentDto.getFieldOfStudy());
        existingStudent.setScholarshipAmount(studentDto.getScholarshipAmount());
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
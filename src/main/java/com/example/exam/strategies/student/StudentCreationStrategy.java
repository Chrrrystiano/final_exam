package com.example.exam.strategies.student;

import com.example.exam.enums.PersonType;
import com.example.exam.exceptions.FailedValidationException;
import com.example.exam.model.pensioner.command.CreatePensionerCommand;
import com.example.exam.model.person.Person;
import com.example.exam.model.person.command.CreatePersonCommand;
import com.example.exam.model.student.Student;
import com.example.exam.model.student.command.CreateStudentCommand;
import com.example.exam.model.student.dto.StudentDto;
import com.example.exam.repository.StudentRepository;
import com.example.exam.service.StudentService;
import com.example.exam.strategies.person.PersonCreationStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class StudentCreationStrategy implements PersonCreationStrategy<CreatePersonCommand> {

    private final StudentRepository studentRepository;
    private final StudentService studentService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public Person createPerson(CreatePersonCommand createPersonCommand) {
        CreateStudentCommand studentCommand = objectMapper.convertValue(createPersonCommand.getParameters(), CreateStudentCommand.class);
        validateParameters(studentCommand);
        Student student = convertToEntity(studentCommand);
        studentRepository.save(student);
        return student;
    }

    private static void validateParameters(CreateStudentCommand studentCommand) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<CreateStudentCommand>> violations = validator.validate(studentCommand);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<CreateStudentCommand> violation : violations) {
                sb.append(violation.getMessage()).append("; ");
            }
            throw new FailedValidationException("Validation failed: " + sb.toString());
        }
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

    private Student convertToEntity(CreateStudentCommand createStudentCommand) {
        return Student.builder()
                .name(createStudentCommand.getName())
                .surname(createStudentCommand.getSurname())
                .pesel(createStudentCommand.getPesel())
                .height(createStudentCommand.getHeight())
                .weight(createStudentCommand.getWeight())
                .email(createStudentCommand.getEmail())
                .universityName(createStudentCommand.getUniversityName())
                .yearOfStudy(createStudentCommand.getYearOfStudy())
                .fieldOfStudy(createStudentCommand.getFieldOfStudy())
                .scholarshipAmount(createStudentCommand.getScholarshipAmount())
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
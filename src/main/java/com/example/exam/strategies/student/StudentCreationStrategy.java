package com.example.exam.strategies.student;

import com.example.exam.enums.PersonType;
import com.example.exam.exceptions.FailedValidationException;
import com.example.exam.model.person.Person;
import com.example.exam.model.person.command.CreatePersonCommand;
import com.example.exam.model.person.command.UpdatePersonCommand;
import com.example.exam.model.student.Student;
import com.example.exam.model.student.command.CreateStudentCommand;
import com.example.exam.model.student.command.UpdateStudentCommand;
import com.example.exam.repository.StudentRepository;
import com.example.exam.service.StudentService;
import com.example.exam.strategies.person.PersonCreationStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
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
    public Person updatePerson(Person existingPerson, UpdatePersonCommand updatePersonCommand) {
        Student student = (Student) existingPerson;
        Map<String, Object> parameters = updatePersonCommand.getParameters();
        UpdateStudentCommand updateStudentCommand = objectMapper.convertValue(parameters, UpdateStudentCommand.class);
        validateUpdateParameters(updateStudentCommand);

        student.setName(updateStudentCommand.getName());
        student.setSurname(updateStudentCommand.getSurname());
        student.setPesel(updateStudentCommand.getPesel());
        student.setHeight(updateStudentCommand.getHeight());
        student.setWeight(updateStudentCommand.getWeight());
        student.setEmail(updateStudentCommand.getEmail());
        student.setUniversityName(updateStudentCommand.getUniversityName());
        student.setYearOfStudy(updateStudentCommand.getYearOfStudy());
        student.setFieldOfStudy(updateStudentCommand.getFieldOfStudy());
        student.setScholarshipAmount(updateStudentCommand.getScholarshipAmount());

        studentRepository.save(student);

        return student;
    }

    private static void validateUpdateParameters(UpdateStudentCommand updateStudentCommand) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<UpdateStudentCommand>> violations = validator.validate(updateStudentCommand);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<UpdateStudentCommand> violation : violations) {
                sb.append(violation.getMessage()).append("; ");
            }
            throw new FailedValidationException("Validation failed: " + sb.toString());
        }
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
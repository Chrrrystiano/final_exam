package com.example.exam.handlers;

import com.example.exam.exceptions.*;
import com.example.exam.exceptions.others.ErrorDetails;
import com.example.exam.exceptions.others.ErrorMessage;
import com.example.exam.exceptions.others.ValidationInformation;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PessimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import jakarta.validation.ConstraintViolationException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    private ResponseEntity<ErrorMessage> createErrorResponse(Exception ex, HttpServletRequest request, HttpStatus status) {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .dateTime(LocalDateTime.now())
                .code(status.value())
                .status(status.getReasonPhrase())
                .message(ex.getMessage())
                .uri(request.getRequestURI())
                .method(request.getMethod())
                .build();
        return new ResponseEntity<>(errorMessage, status);
    }

    @ExceptionHandler(UnsupportedPersonTypeException.class)
    public ResponseEntity<?> handleUnsupportedPersonTypeException(UnsupportedPersonTypeException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PeselValidationException.class)
    public ResponseEntity<String> handlePeselValidationException(PeselValidationException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(EmailValidationException.class)
    public ResponseEntity<String> handleEmailValidationException(EmailValidationException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DateValidationException.class)
    public ResponseEntity<String> handleDateValidationException(DateValidationException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoCurrentPositionException.class)
    public ResponseEntity<String> handleNoCurrentPositionException(NoCurrentPositionException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<String> handleEmployeeNotFoundException(EmployeeNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<String> handleStudentNotFoundException(StudentNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PensionerNotFoundException.class)
    public ResponseEntity<String> handlePensionerNotFoundException(PensionerNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<String> handleOptimisticLockException() {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("The data has been modified. Please reload and try again.");
    }

    @ExceptionHandler(PessimisticLockException.class)
    public ResponseEntity<Object> handlePessimisticLockException(PessimisticLockException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", "The operation failed due to a locking conflict. Please reload and try again.");
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(LockTimeoutException.class)
    public ResponseEntity<Object> handleLockTimeoutException(LockTimeoutException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", "The operation time has been exceeded. Please reload and try again.");
        return new ResponseEntity<>(body, HttpStatus.REQUEST_TIMEOUT);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxUploadSizeExceededException() {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Your file is too large");
    }

    @ExceptionHandler(PersonNotFoundException.class)
    public ResponseEntity<?> handlePersonNotFoundException(PersonNotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""),
                "GET");
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException e) {
        List<String> errors = e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toList());
        Map<String, Object> response = new HashMap<>();
        response.put("errors", errors);
        response.put("status", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<List<ValidationInformation>> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(
                ex.getFieldErrors().stream()
                        .map(
                                fieldError -> new ValidationInformation(fieldError.getDefaultMessage(), fieldError.getField())
                        ).toList());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        Throwable rootCause = e.getMostSpecificCause();
        String message = rootCause.getMessage();
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());

        if (message.contains("persons_pesel_key")) {
            body.put("error", "This pesel number is already assigned to the user in the database.");
            body.put("status", HttpStatus.BAD_REQUEST.value());
        } else if (message.contains("persons_email_key")) {
            body.put("error", "This email address is already assigned to the user in the database");
            body.put("status", HttpStatus.BAD_REQUEST.value());
        } else {
            body.put("error", "Internal server error: " + message);
            body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return new ResponseEntity<>(body, HttpStatus.valueOf((Integer) body.get("status")));
    }

    @ExceptionHandler(FailedImportFileException.class)
    public ResponseEntity<?> handleFailedImportFileException(FailedImportFileException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false),
                request.getContextPath());

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

}

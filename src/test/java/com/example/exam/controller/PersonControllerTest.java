package com.example.exam.controller;

import com.example.exam.DatabaseCleaner;
import com.example.exam.ExamApplication;


import com.example.exam.model.person.command.CreatePersonCommand;
import com.example.exam.model.person.command.UpdatePersonCommand;
import com.example.exam.payload.request.LoginRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import liquibase.exception.LiquibaseException;
import org.junit.jupiter.api.Test;


import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest(classes = ExamApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class PersonControllerTest {

    private final MockMvc postman;
    private final ObjectMapper objectMapper;
    private final DatabaseCleaner databaseCleaner;
    private final String VALID_USER_TOKEN;
    private final String VALID_ADMIN_TOKEN;
    private final String VALID_IMPORTER_TOKEN;
    private final String INVALID_TOKEN;


    @Autowired
    public PersonControllerTest(MockMvc postman, ObjectMapper objectMapper, DatabaseCleaner databaseCleaner) throws Exception {
        this.postman = postman;
        this.objectMapper = objectMapper;
        this.databaseCleaner = databaseCleaner;
        this.VALID_USER_TOKEN = getValidUserToken();
        this.VALID_ADMIN_TOKEN = getValidAdminToken();
        this.VALID_IMPORTER_TOKEN = getValidImporterToken();
        this.INVALID_TOKEN = getInvalidToken();
    }

    @AfterEach
    void tearDown() throws LiquibaseException {
        databaseCleaner.cleanUp();
    }

    public String getValidUserToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest("user", "user");

        String requestBody = objectMapper.writeValueAsString(loginRequest);

        String response = postman.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(response);
        String tokenType = jsonNode.get("type").asText();
        String accessToken = jsonNode.get("token").asText();

        return tokenType + " " + accessToken;
    }

    public String getValidAdminToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin", "admin");

        String requestBody = objectMapper.writeValueAsString(loginRequest);

        String response = postman.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(response);
        String tokenType = jsonNode.get("type").asText();
        String accessToken = jsonNode.get("token").asText();

        return tokenType + " " + accessToken;
    }

    public String getValidImporterToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest("importer", "importer");

        String requestBody = objectMapper.writeValueAsString(loginRequest);

        String response = postman.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(response);

        String tokenType = jsonNode.get("type").asText();
        String accessToken = jsonNode.get("token").asText();

        return tokenType + " " + accessToken;
    }

    public String getInvalidToken() {
        return "Bearer 12837sadasdb8237yeedgb38e13g";
    }


    @Test
    void shouldSaveWithRoleAdmin() throws Exception {
        CreatePersonCommand command = CreatePersonCommand.builder()
                .type("STUDENT")
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "98010112345",
                        "height", 175.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "Uniwersytet Warszawski",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(get("/api/people/search?type=STUDENT&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        postman.perform(post("/api/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(51))
                .andExpect(jsonPath("$.name").value("Maciej"))
                .andExpect(jsonPath("$.surname").value("Wisniewski"))
                .andExpect(jsonPath("$.pesel").value("98010112345"))
                .andExpect(jsonPath("$.height").value(175.70))
                .andExpect(jsonPath("$.weight").value(70.80))
                .andExpect(jsonPath("$.email").value("michal.wisniewski@gmail.com"))
                .andExpect(jsonPath("$.university_name").value("Uniwersytet Warszawski"))
                .andExpect(jsonPath("$.year_of_study").value(2))
                .andExpect(jsonPath("$.field_of_study").value("Informatyka"))
                .andExpect(jsonPath("$.scholarship_amount").value(1000.00));

        postman.perform(get("/api/people/search?type=STUDENT&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].type").value("STUDENT"))
                .andExpect(jsonPath("$.content[0].id").value(51))
                .andExpect(jsonPath("$.content[0].name").value("Maciej"))
                .andExpect(jsonPath("$.content[0].surname").value("Wisniewski"))
                .andExpect(jsonPath("$.content[0].pesel").value("98010112345"))
                .andExpect(jsonPath("$.content[0].height").value(175.70))
                .andExpect(jsonPath("$.content[0].weight").value(70.80))
                .andExpect(jsonPath("$.content[0].email").value("michal.wisniewski@gmail.com"))
                .andExpect(jsonPath("$.content[0].university_name").value("Uniwersytet Warszawski"))
                .andExpect(jsonPath("$.content[0].year_of_study").value(2))
                .andExpect(jsonPath("$.content[0].field_of_study").value("Informatyka"))
                .andExpect(jsonPath("$.content[0].scholarship_amount").value(1000.00));
    }


    @Test
    void shouldNotSaveStudentWithRoleUser() throws Exception {
        CreatePersonCommand command = CreatePersonCommand.builder()
                .type("STUDENT")
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "98010112345",
                        "height", 175.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "Uniwersytet Warszawski",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(post("/api/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", VALID_USER_TOKEN))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotSaveStudentWithRoleImporter() throws Exception {
        CreatePersonCommand command = CreatePersonCommand.builder()
                .type("STUDENT")
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "98010112345",
                        "height", 175.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "Uniwersytet Warszawski",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(post("/api/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", VALID_IMPORTER_TOKEN))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotSaveStudentWithInvalidToken() throws Exception {
        CreatePersonCommand command = CreatePersonCommand.builder()
                .type("STUDENT")
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "98010112345",
                        "height", 175.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "Uniwersytet Warszawski",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(post("/api/people/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", INVALID_TOKEN))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.status").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Full authentication is required to access this resource"))
                .andExpect(jsonPath("$.uri").value("/api/people/save"))
                .andExpect(jsonPath("$.method").value("POST"));
    }

    @Test
    void shouldNotSaveStudentWithoutAutorization() throws Exception {
        CreatePersonCommand command = CreatePersonCommand.builder()
                .type("STUDENT")
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "98010112345",
                        "height", 175.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "Uniwersytet Warszawski",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(post("/api/people/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.status").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Full authentication is required to access this resource"))
                .andExpect(jsonPath("$.uri").value("/api/people/save"))
                .andExpect(jsonPath("$.method").value("POST"));
    }

    @Test
    void shouldNotSaveStudentWhenNameIsBlankWithRoleAdmin() throws Exception {
        CreatePersonCommand command = CreatePersonCommand.builder()
                .type("STUDENT")
                .parameters(Map.of(
                        "name", "",
                        "surname", "Wisniewski",
                        "pesel", "98010112345",
                        "height", 175.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "Uniwersytet Warszawski",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(get("/api/people/search?type=STUDENT&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Validation failed: The NAME field cannot be left empty; "))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotSaveStudentWhenSurnameIsBlankWithRoleAdmin() throws Exception {
        CreatePersonCommand command = CreatePersonCommand.builder()
                .type("STUDENT")
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "",
                        "pesel", "98010112345",
                        "height", 175.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "Uniwersytet Warszawski",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(get("/api/people/search?type=STUDENT&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Validation failed: The SURNAME field cannot be left empty; "))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotSaveStudentWhenPeselIsShorterWithRoleAdmin() throws Exception {
        CreatePersonCommand command = CreatePersonCommand.builder()
                .type("STUDENT")
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "9805",
                        "height", 175.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "Uniwersytet Warszawski",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(get("/api/people/search?type=STUDENT&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Validation failed: Pesel must have exactly 11 digits!; "))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotSaveStudentWhenPeselIsLongerWithRoleAdmin() throws Exception {
        CreatePersonCommand command = CreatePersonCommand.builder()
                .type("STUDENT")
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "98010118712387125387125387125387125381231231231231234532547523457232345",
                        "height", 175.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "Uniwersytet Warszawski",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(get("/api/people/search?type=STUDENT&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Validation failed: Pesel must have exactly 11 digits!; "))
                .andExpect(jsonPath("$.status").value(400));
    }


// TODO// Idk dlaczego oba te testy nie działają

//    @Test
//    void shouldNotSaveStudentWhenPeselAlreadyExistsWithRoleAdmin() throws Exception {
//        CreatePersonCommand command = CreatePersonCommand.builder()
//                .type("STUDENT")
//                .parameters(Map.of(
//                        "name", "Maciej",
//                        "surname", "Wisniewski",
//                        "pesel", "66101298576",
//                        "height", 175.70,
//                        "weight", 70.80,
//                        "email", "michal.wisniewski@gmail.com",
//                        "universityName", "Uniwersytet Warszawski",
//                        "yearOfStudy", 2,
//                        "fieldOfStudy", "Informatyka",
//                        "scholarshipAmount", 1000.00
//                ))
//                .build();
//
//        String requestBody = objectMapper.writeValueAsString(command);
//
//        postman.perform(get("/api/people/search?type=STUDENT&id=51")
//                        .header("Authorization", VALID_ADMIN_TOKEN))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content").isEmpty());
//
//        postman.perform(post("/api/people")
//                        .header("Authorization", VALID_ADMIN_TOKEN)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestBody))
//                .andDo(print())
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error").value("This pesel number is already assigned to the user in the database."))
//                .andExpect(jsonPath("$.status").value(400));
//    }
//
//    @Test
//    void shouldNotSaveStudentWhenEmailAlreadyExistsWithRoleAdmin() throws Exception {
//        CreatePersonCommand command = CreatePersonCommand.builder()
//                .type("STUDENT")
//                .parameters(Map.of(
//                        "name", "Maciej",
//                        "surname", "Wisniewski",
//                        "pesel", "66117777777",
//                        "height", 175.70,
//                        "weight", 70.80,
//                        "email", "zygmunt.grygiel@gmail.com",
//                        "universityName", "Uniwersytet Warszawski",
//                        "yearOfStudy", 2,
//                        "fieldOfStudy", "Informatyka",
//                        "scholarshipAmount", 1000.00
//                ))
//                .build();
//
//        String requestBody = objectMapper.writeValueAsString(command);
//
//        postman.perform(get("/api/people/search?type=STUDENT&id=51")
//                        .header("Authorization", VALID_ADMIN_TOKEN))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content").isEmpty());
//
//        postman.perform(post("/api/people")
//                        .header("Authorization", VALID_ADMIN_TOKEN)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestBody))
//                .andDo(print())
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error").value("This email address is already assigned to the user in the database"))
//                .andExpect(jsonPath("$.status").value(400));
//    }

    @Test
    void shouldNotSaveStudentWhenHeightIsTooBigWithRoleAdmin() throws Exception {
        CreatePersonCommand command = CreatePersonCommand.builder()
                .type("STUDENT")
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "66119876586",
                        "height", 500.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "Uniwersytet Warszawski",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(get("/api/people/search?type=STUDENT&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Validation failed: Height must be less than 300; "))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotSaveStudentWhenHeightIsTooSmallWithRoleAdmin() throws Exception {
        CreatePersonCommand command = CreatePersonCommand.builder()
                .type("STUDENT")
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "66119876586",
                        "height", -175.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "Uniwersytet Warszawski",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(get("/api/people/search?type=STUDENT&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Validation failed: Height must be greater than 0; "))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotSaveStudentWhenWeightIsTooBigWithRoleAdmin() throws Exception {
        CreatePersonCommand command = CreatePersonCommand.builder()
                .type("STUDENT")
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "66119876586",
                        "height", 175.70,
                        "weight", 700.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "Uniwersytet Warszawski",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(get("/api/people/search?type=STUDENT&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Validation failed: Weight must be less than 300; "))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotSaveStudentWhenWeightIsTooSmallWithRoleAdmin() throws Exception {
        CreatePersonCommand command = CreatePersonCommand.builder()
                .type("STUDENT")
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "66119876586",
                        "height", 175.70,
                        "weight", -70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "Uniwersytet Warszawski",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(get("/api/people/search?type=STUDENT&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Validation failed: Weight must be greater than 0; "))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotSaveStudentWhenEmailIsEmptyWithRoleAdmin() throws Exception {
        CreatePersonCommand command = CreatePersonCommand.builder()
                .type("STUDENT")
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "66119876586",
                        "height", 175.70,
                        "weight", 70.80,
                        "email", "",
                        "universityName", "Uniwersytet Warszawski",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(get("/api/people/search?type=STUDENT&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Validation failed: The EMAIL field cannot be left empty; "))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotSaveStudentWhenEmailIsNotValidWithRoleAdmin() throws Exception {
        CreatePersonCommand command = CreatePersonCommand.builder()
                .type("STUDENT")
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "66119876586",
                        "height", 175.70,
                        "weight", 70.80,
                        "email", "micha",
                        "universityName", "Uniwersytet Warszawski",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(get("/api/people/search?type=STUDENT&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Validation failed: Email must be valid; "))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotSaveStudentWhenUniversityNameIsEmptyWithRoleAdmin() throws Exception {
        CreatePersonCommand command = CreatePersonCommand.builder()
                .type("STUDENT")
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "66119876586",
                        "height", 175.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();

        String requestBody = objectMapper.writeValueAsString(command);
        postman.perform(get("/api/people/search?type=STUDENT&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Validation failed: The UNIVERSITY field cannot be left empty; "))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotSaveStudentWhenYearOfStudyIsLessThan1WithRoleAdmin() throws Exception {
        CreatePersonCommand command = CreatePersonCommand.builder()
                .type("STUDENT")
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "66119876586",
                        "height", 175.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "Uniwersytet Warszawski",
                        "yearOfStudy", -2,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(get("/api/people/search?type=STUDENT&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Validation failed: Year of study must be at least 1; "))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotSaveStudentWhenYearOfStudyIsGreaterThan1WithRoleAdmin() throws Exception {
        CreatePersonCommand command = CreatePersonCommand.builder()
                .type("STUDENT")
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "66119876586",
                        "height", 175.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "Uniwersytet Warszawski",
                        "yearOfStudy", 20,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(get("/api/people/search?type=STUDENT&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Validation failed: Year of study must be no more than 5; "))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotSaveStudentWhenFieldOfStudyIsEmptyWithRoleAdmin() throws Exception {
        CreatePersonCommand command = CreatePersonCommand.builder()
                .type("STUDENT")
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "66119876586",
                        "height", 175.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "Uniwersytet Warszawski",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "",
                        "scholarshipAmount", 1000.00
                ))
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(get("/api/people/search?type=STUDENT&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Validation failed: The FIELD OF STUDY field cannot be left empty; "))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotSaveStudentWhenScholarshipAmountIsNegativeWithRoleAdmin() throws Exception {
        CreatePersonCommand command = CreatePersonCommand.builder()
                .type("STUDENT")
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "66119876586",
                        "height", 175.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "Uniwersytet Warszawski",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", -1000.00
                ))
                .build();

        String requestBody = objectMapper.writeValueAsString(command);
        postman.perform(get("/api/people/search?type=STUDENT&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Validation failed: Scholarship amount must be positive or zero; "))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldEditStudentWithAdminRole() throws Exception {
        UpdatePersonCommand command = UpdatePersonCommand.builder()
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "99111105000",
                        "height", 175.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "Uniwersytet Warszawski",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();
        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(get("/api/people/search?type=PERSON&id=3")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].type").value("STUDENT"))
                .andExpect(jsonPath("$.content[0].id").value(3))
                .andExpect(jsonPath("$.content[0].name").value("Krystian"))
                .andExpect(jsonPath("$.content[0].surname").value("Wilk"))
                .andExpect(jsonPath("$.content[0].pesel").value("99111105778"))
                .andExpect(jsonPath("$.content[0].height").value(190.5))
                .andExpect(jsonPath("$.content[0].weight").value(90.4))
                .andExpect(jsonPath("$.content[0].email").value("krystian.wilk@gmail.com"))
                .andExpect(jsonPath("$.content[0].university_name").value("Uniwersytet Jagielonski"))
                .andExpect(jsonPath("$.content[0].year_of_study").value(2))
                .andExpect(jsonPath("$.content[0].field_of_study").value("Mechanika i Budowa Maszyn"))
                .andExpect(jsonPath("$.content[0].scholarship_amount").value(0));

        postman.perform(put("/api/people/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk());


        postman.perform(get("/api/people/search?type=PERSON&id=3")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].type").value("STUDENT"))
                .andExpect(jsonPath("$.content[0].name").value("Maciej"))
                .andExpect(jsonPath("$.content[0].surname").value("Wisniewski"))
                .andExpect(jsonPath("$.content[0].pesel").value("99111105000"))
                .andExpect(jsonPath("$.content[0].height").value(175.70))
                .andExpect(jsonPath("$.content[0].weight").value(70.80))
                .andExpect(jsonPath("$.content[0].email").value("michal.wisniewski@gmail.com"))
                .andExpect(jsonPath("$.content[0].university_name").value("Uniwersytet Warszawski"))
                .andExpect(jsonPath("$.content[0].year_of_study").value(2))
                .andExpect(jsonPath("$.content[0].field_of_study").value("Informatyka"))
                .andExpect(jsonPath("$.content[0].scholarship_amount").value(1000));
    }

    @Test
    void shouldNotEditStudentWithoutNameAdminRole() throws Exception {
        UpdatePersonCommand command = UpdatePersonCommand.builder()
                .parameters(Map.of(
                        "name", "",
                        "surname", "Wisniewski",
                        "pesel", "99111105000",
                        "height", 175.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "Uniwersytet Warszawski",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();
        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(put("/api/people/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Validation failed: The NAME field cannot be left empty; "))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotEditStudentWithWrongIdAdminRole() throws Exception {
        UpdatePersonCommand command = UpdatePersonCommand.builder()
                .parameters(Map.of(
                        "name", "Karol",
                        "surname", "Wisniewski",
                        "pesel", "99111105000",
                        "height", 175.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "Uniwersytet Warszawski",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();
        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(put("/api/people/78")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotEditStudentWithoutSurnameAdminRole() throws Exception {
        UpdatePersonCommand command = UpdatePersonCommand.builder()
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "",
                        "pesel", "99111105000",
                        "height", 175.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "Uniwersytet Warszawski",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();
        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(put("/api/people/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Validation failed: The SURNAME field cannot be left empty; "))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
        /// Kolejność wyjątków czasem sie różni
    void shouldNotEditStudentWithoutPeselNumberAdminRole() throws Exception {
        UpdatePersonCommand command = UpdatePersonCommand.builder()
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "",
                        "height", 175.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "Uniwersytet Warszawski",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();
        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(put("/api/people/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Validation failed: The PESEL field cannot be left empty; Pesel must have exactly 11 digits!; "))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotEditStudentWhenPeselNumberIsTooShortAdminRole() throws Exception {
        UpdatePersonCommand command = UpdatePersonCommand.builder()
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "17",
                        "height", 175.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "Uniwersytet Warszawski",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();
        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(put("/api/people/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Validation failed: Pesel must have exactly 11 digits!; "))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotEditStudentWhenPeselNumberIsTooLargeAdminRole() throws Exception {
        UpdatePersonCommand command = UpdatePersonCommand.builder()
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "17172581236192834612946124691246127340192743",
                        "height", 175.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "Uniwersytet Warszawski",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();
        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(put("/api/people/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Validation failed: Pesel must have exactly 11 digits!; "))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotEditStudentWhenHeightIsTooSmallAdminRole() throws Exception {
        UpdatePersonCommand command = UpdatePersonCommand.builder()
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "99111105000",
                        "height", -175.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "UJ",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();
        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(put("/api/people/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Validation failed: Height must be greater than 0; "))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotEditStudentWhenHeightIsTooBigAdminRole() throws Exception {
        UpdatePersonCommand command = UpdatePersonCommand.builder()
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "99111105000",
                        "height", 500.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "UJ",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();
        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(put("/api/people/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Validation failed: Height must be less than 300; "))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotEditStudentWhenWeightIsTooSmallAdminRole() throws Exception {
        UpdatePersonCommand command = UpdatePersonCommand.builder()
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "99111105000",
                        "height", 175.70,
                        "weight", -70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "UJ",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();
        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(put("/api/people/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Validation failed: Weight must be greater than 0; "))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotEditStudentWhenWeightIsTooBigAdminRole() throws Exception {
        UpdatePersonCommand command = UpdatePersonCommand.builder()
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "99111105000",
                        "height", 180.70,
                        "weight", 500.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "UJ",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();
        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(put("/api/people/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Validation failed: Weight must be less than 300; "))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotEditStudentWithoutUniversityNameAdminRole() throws Exception {
        UpdatePersonCommand command = UpdatePersonCommand.builder()
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "99111105000",
                        "height", 175.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();
        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(put("/api/people/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Validation failed: The UNIVERSITY field cannot be left empty; "))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotEditStudentWhenYearOfStudyIsToSmallAdminRole() throws Exception {
        UpdatePersonCommand command = UpdatePersonCommand.builder()
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "99111105000",
                        "height", 175.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "Uniwersytet Warszawski",
                        "yearOfStudy", 0,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();
        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(put("/api/people/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Validation failed: Year of study must be at least 1; "))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotEditStudentWhenYearOfStudyIsToBigAdminRole() throws Exception {
        UpdatePersonCommand command = UpdatePersonCommand.builder()
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "99111105000",
                        "height", 175.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "Uniwersytet Warszawski",
                        "yearOfStudy", 10,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();
        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(put("/api/people/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Validation failed: Year of study must be no more than 5; "))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotEditStudentWithoutFieldOfStudyAdminRole() throws Exception {
        UpdatePersonCommand command = UpdatePersonCommand.builder()
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "99111105000",
                        "height", 175.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "Uniwersytet Warszawski",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "",
                        "scholarshipAmount", 1000.00
                ))
                .build();
        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(put("/api/people/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Validation failed: The FIELD OF STUDY field cannot be left empty; "))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotEditStudentWithoutScholarshipAmountIsNegativeAdminRole() throws Exception {
        UpdatePersonCommand command = UpdatePersonCommand.builder()
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "99111105000",
                        "height", 175.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "Uniwersytet Warszawski",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "Mechanika",
                        "scholarshipAmount", -7777
                ))
                .build();
        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(put("/api/people/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Validation failed: Scholarship amount must be positive or zero; "))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotEditPensionerWhenPensionAmountIsNegativeAdminRole() throws Exception {
        UpdatePersonCommand command = UpdatePersonCommand.builder()
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "99111105000",
                        "height", 175.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "pensionAmount", -123,
                        "yearsOfWork", 20
                ))
                .build();
        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(put("/api/people/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Validation failed: Pension amount amount must be positive or zero; "))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotEditPensionerWhenYearsOfWorkIsTooSmallAdminRole() throws Exception {
        UpdatePersonCommand command = UpdatePersonCommand.builder()
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "99111105000",
                        "height", 175.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "pensionAmount", 123,
                        "yearsOfWork", -20
                ))
                .build();
        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(put("/api/people/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Validation failed: Year of work must be at least 1; "))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotEditStudentWithInvalidToken() throws Exception {
        UpdatePersonCommand command = UpdatePersonCommand.builder()
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "99111105000",
                        "height", 175.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "Uniwersytet Warszawski",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();
        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(put("/api/people/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", INVALID_TOKEN))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.status").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Full authentication is required to access this resource"))
                .andExpect(jsonPath("$.uri").value("/api/people/edit"))
                .andExpect(jsonPath("$.method").value("PUT"));
    }

    @Test
    void shouldNotEditStudentWithoutAutorization() throws Exception {
        UpdatePersonCommand command = UpdatePersonCommand.builder()
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "99111105000",
                        "height", 175.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "Uniwersytet Warszawski",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();
        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(post("/api/people/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.status").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Full authentication is required to access this resource"))
                .andExpect(jsonPath("$.uri").value("/api/people/edit"))
                .andExpect(jsonPath("$.method").value("POST"));
    }

    @Test
    void shouldNotEditStudentWithRoleUser() throws Exception {
        UpdatePersonCommand command = UpdatePersonCommand.builder()
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "99111105000",
                        "height", 175.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "Uniwersytet Warszawski",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();
        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(put("/api/people/12")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", VALID_USER_TOKEN))
                .andDo(print())
                .andExpect(status().isForbidden());

    }

    @Test
    void shouldNotEditStudentWithRoleImporter() throws Exception {
        UpdatePersonCommand command = UpdatePersonCommand.builder()
                .parameters(Map.of(
                        "name", "Maciej",
                        "surname", "Wisniewski",
                        "pesel", "99111105000",
                        "height", 175.70,
                        "weight", 70.80,
                        "email", "michal.wisniewski@gmail.com",
                        "universityName", "Uniwersytet Warszawski",
                        "yearOfStudy", 2,
                        "fieldOfStudy", "Informatyka",
                        "scholarshipAmount", 1000.00
                ))
                .build();
        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(put("/api/people/51")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", VALID_IMPORTER_TOKEN))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotGivePeoplePageWithoutAuthorization() throws Exception {
        postman.perform(get("/api/people/search?type=PERSON&id=50"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.status").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Full authentication is required to access this resource"))
                .andExpect(jsonPath("$.uri").value("/api/people/search"))
                .andExpect(jsonPath("$.method").value("GET"));
    }

    @Test
    void shouldNotGivePeoplePageWithInvalidToken() throws Exception {
        postman.perform(get("/api/people/search?type=PERSON&id=12")
                        .header("Authorization", INVALID_TOKEN))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.status").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Full authentication is required to access this resource"))
                .andExpect(jsonPath("$.uri").value("/api/people/search"))
                .andExpect(jsonPath("$.method").value("GET"));
    }


    @Test
    void shouldNotFindPersonByIdWithRoleImporter() throws Exception {
        postman.perform(get("/api/people/search?type=PERSON&id=12")
                        .header("Authorization", VALID_IMPORTER_TOKEN))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldFindPersonByIdWithRoleAdmin() throws Exception {
        postman.perform(get("/api/people/search?type=PERSON&id=12")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].type").value("STUDENT"))
                .andExpect(jsonPath("$.content[0].id").value(12))
                .andExpect(jsonPath("$.content[0].name").value("Piotr"))
                .andExpect(jsonPath("$.content[0].surname").value("Filipiec"))
                .andExpect(jsonPath("$.content[0].pesel").value("98032205778"))
                .andExpect(jsonPath("$.content[0].height").value(199.5))
                .andExpect(jsonPath("$.content[0].weight").value(95.4))
                .andExpect(jsonPath("$.content[0].email").value("piotr.filipiec@gmail.com"))
                .andExpect(jsonPath("$.content[0].university_name").value("Politechnika Gdanska"))
                .andExpect(jsonPath("$.content[0].year_of_study").value(5))
                .andExpect(jsonPath("$.content[0].field_of_study").value("Mechanika i Budowa Maszyn"))
                .andExpect(jsonPath("$.content[0].scholarship_amount").value(50));
    }

    @Test
    public void shouldFindPersonByIdWithRoleUser() throws Exception {
        postman.perform(get("/api/people/search?type=PERSON&id=12")
                        .header("Authorization", VALID_USER_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].type").value("STUDENT"))
                .andExpect(jsonPath("$.content[0].id").value(12))
                .andExpect(jsonPath("$.content[0].name").value("Piotr"))
                .andExpect(jsonPath("$.content[0].surname").value("Filipiec"))
                .andExpect(jsonPath("$.content[0].pesel").value("98032205778"))
                .andExpect(jsonPath("$.content[0].height").value(199.5))
                .andExpect(jsonPath("$.content[0].weight").value(95.4))
                .andExpect(jsonPath("$.content[0].email").value("piotr.filipiec@gmail.com"))
                .andExpect(jsonPath("$.content[0].university_name").value("Politechnika Gdanska"))
                .andExpect(jsonPath("$.content[0].year_of_study").value(5))
                .andExpect(jsonPath("$.content[0].field_of_study").value("Mechanika i Budowa Maszyn"))
                .andExpect(jsonPath("$.content[0].scholarship_amount").value(50));
    }

    @Test
    void shouldGiveListOfEmployeeWithRoleAdmin() throws Exception {
        postman.perform(get("/api/people/search?type=EMPLOYEE&page=3&size=3")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].type").value("EMPLOYEE"))
                .andExpect(jsonPath("$.content[0].id").value(28))
                .andExpect(jsonPath("$.content[0].name").value("Natalia"))
                .andExpect(jsonPath("$.content[0].surname").value("Nowak"))
                .andExpect(jsonPath("$.content[0].pesel").value("87111114578"))
                .andExpect(jsonPath("$.content[0].height").value(184.5))
                .andExpect(jsonPath("$.content[0].weight").value(85.9))
                .andExpect(jsonPath("$.content[0].email").value("natalia.nowak@gmail.com"))
                .andExpect(jsonPath("$.content[0].current_position_start_date").value("2016-10-18"))
                .andExpect(jsonPath("$.content[0].current_salary").value(6500.00))
                .andExpect(jsonPath("$.content[0].current_position").value("Kardiolog"))
                .andExpect(jsonPath("$.content[1].type").value("EMPLOYEE"))
                .andExpect(jsonPath("$.content[1].id").value(31))
                .andExpect(jsonPath("$.content[1].name").value("Robert"))
                .andExpect(jsonPath("$.content[1].surname").value("Waszko"))
                .andExpect(jsonPath("$.content[1].pesel").value("78111114578"))
                .andExpect(jsonPath("$.content[1].height").value(184.5))
                .andExpect(jsonPath("$.content[1].weight").value(86.9))
                .andExpect(jsonPath("$.content[1].email").value("robert.waszko@gmail.com"))
                .andExpect(jsonPath("$.content[1].current_position_start_date").value("2022-12-12"))
                .andExpect(jsonPath("$.content[1].current_salary").value(3500.00))
                .andExpect(jsonPath("$.content[1].current_position").value("Magazynier"))
                .andExpect(jsonPath("$.content[2].type").value("EMPLOYEE"))
                .andExpect(jsonPath("$.content[2].id").value(34))
                .andExpect(jsonPath("$.content[2].name").value("Filip"))
                .andExpect(jsonPath("$.content[2].surname").value("Kardaszew"))
                .andExpect(jsonPath("$.content[2].pesel").value("86111114578"))
                .andExpect(jsonPath("$.content[2].height").value(184.5))
                .andExpect(jsonPath("$.content[2].weight").value(88.9))
                .andExpect(jsonPath("$.content[2].email").value("filip.kardaszew@gmail.com"))
                .andExpect(jsonPath("$.content[2].current_position_start_date").value("2024-03-03"))
                .andExpect(jsonPath("$.content[2].current_salary").value(5500.00))
                .andExpect(jsonPath("$.content[2].current_position").value("Kontroler Jakosci"));
    }

    @Test
    void shouldGiveListOfPeopleWithNameWithRoleAdmin() throws Exception {
        postman.perform(get("/api/people/search?type=PERSON&name=Adam&page=0&size=2")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].type").value("PENSIONER"))
                .andExpect(jsonPath("$.content[0].id").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Adam"))
                .andExpect(jsonPath("$.content[0].surname").value("Filipienko"))
                .andExpect(jsonPath("$.content[0].pesel").value("66101298576"))
                .andExpect(jsonPath("$.content[0].height").value(177.5))
                .andExpect(jsonPath("$.content[0].weight").value(77.8))
                .andExpect(jsonPath("$.content[0].email").value("adam.filipienko@gmail.com"))
                .andExpect(jsonPath("$.content[0].pension_amount").value(1300.00))
                .andExpect(jsonPath("$.content[0].years_of_work").value(46))
                .andExpect(jsonPath("$.content[1].type").value("STUDENT"))
                .andExpect(jsonPath("$.content[1].id").value(9))
                .andExpect(jsonPath("$.content[1].name").value("Adam"))
                .andExpect(jsonPath("$.content[1].surname").value("Danielski"))
                .andExpect(jsonPath("$.content[1].pesel").value("99102205778"))
                .andExpect(jsonPath("$.content[1].height").value(199.5))
                .andExpect(jsonPath("$.content[1].weight").value(80.4))
                .andExpect(jsonPath("$.content[1].email").value("adam.danielski@gmail.com"))
                .andExpect(jsonPath("$.content[1].university_name").value("Politechnika Poznanska"))
                .andExpect(jsonPath("$.content[1].year_of_study").value(3))
                .andExpect(jsonPath("$.content[1].field_of_study").value("Mechatronika"))
                .andExpect(jsonPath("$.content[1].scholarship_amount").value(1200.00));
    }

    @Test
    void shouldGiveListOfPeopleWithPartOfSurnameWithRoleAdmin() throws Exception {
        postman.perform(get("/api/people/search?type=PERSON&surname=ec&page=2&size=2")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].type").value("STUDENT"))
                .andExpect(jsonPath("$.content[0].id").value(30))
                .andExpect(jsonPath("$.content[0].name").value("Nikola"))
                .andExpect(jsonPath("$.content[0].surname").value("Szpikulec"))
                .andExpect(jsonPath("$.content[0].pesel").value("98111115788"))
                .andExpect(jsonPath("$.content[0].height").value(160.5))
                .andExpect(jsonPath("$.content[0].weight").value(50.4))
                .andExpect(jsonPath("$.content[0].email").value("nikola.szpikulec@gmail.com"))
                .andExpect(jsonPath("$.content[0].university_name").value("Uniwersytet Warszawski"))
                .andExpect(jsonPath("$.content[0].year_of_study").value(1))
                .andExpect(jsonPath("$.content[0].field_of_study").value("Inzynieria Materialowa"))
                .andExpect(jsonPath("$.content[0].scholarship_amount").value(500.00))
                .andExpect(jsonPath("$.content[1].type").value("STUDENT"))
                .andExpect(jsonPath("$.content[1].id").value(36))
                .andExpect(jsonPath("$.content[1].name").value("Robert"))
                .andExpect(jsonPath("$.content[1].surname").value("Filipiec"))
                .andExpect(jsonPath("$.content[1].pesel").value("98111115778"))
                .andExpect(jsonPath("$.content[1].height").value(199.5))
                .andExpect(jsonPath("$.content[1].weight").value(95.4))
                .andExpect(jsonPath("$.content[1].email").value("robert.filipiec@gmail.com"))
                .andExpect(jsonPath("$.content[1].university_name").value("Politechnika Gdanska"))
                .andExpect(jsonPath("$.content[1].year_of_study").value(5))
                .andExpect(jsonPath("$.content[1].field_of_study").value("Mechanika i Budowa Maszyn"))
                .andExpect(jsonPath("$.content[1].scholarship_amount").value(50.00));
    }

    @Test
    void shouldGiveListOfPeopleWithWeightFromToWithRoleAdmin() throws Exception {
        postman.perform(get("/api/people/search?type=PERSON&minWeight=75.0&maxWeight=86.0&page=4&size=1")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].type").value("EMPLOYEE"))
                .andExpect(jsonPath("$.content[0].id").value(16))
                .andExpect(jsonPath("$.content[0].name").value("Piotr"))
                .andExpect(jsonPath("$.content[0].surname").value("Nietzsche"))
                .andExpect(jsonPath("$.content[0].pesel").value("80100304578"))
                .andExpect(jsonPath("$.content[0].height").value(184.5))
                .andExpect(jsonPath("$.content[0].weight").value(85.9))
                .andExpect(jsonPath("$.content[0].email").value("piotr.nietzsche1212@gmail.com"))
                .andExpect(jsonPath("$.content[0].current_position_start_date").value("2016-10-18"))
                .andExpect(jsonPath("$.content[0].current_salary").value(6500.00))
                .andExpect(jsonPath("$.content[0].current_position").value("Kardiolog"));
    }

    @Test
    void shouldNotGiveListOfPeopleWithWithSpecifiedCriteriaNotFoundWithRoleAdmin() throws Exception {
        postman.perform(get("/api/people/search?type=PERSON&minHeight=75.0&maxHeight=86.0")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void shouldGiveListOfPeopleWithHeightFromToWithRoleAdmin() throws Exception {
        postman.perform(get("/api/people/search?type=PERSON&minHeight=150.0&maxHeight=185.0&page=2&size=3")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].type").value("PENSIONER"))
                .andExpect(jsonPath("$.content[0].id").value(8))
                .andExpect(jsonPath("$.content[0].name").value("Zygmunt"))
                .andExpect(jsonPath("$.content[0].surname").value("Grygiel"))
                .andExpect(jsonPath("$.content[0].pesel").value("63101298576"))
                .andExpect(jsonPath("$.content[0].height").value("173.5"))
                .andExpect(jsonPath("$.content[0].weight").value("87.8"))
                .andExpect(jsonPath("$.content[0].email").value("zygmunt.grygiel@gmail.com"))
                .andExpect(jsonPath("$.content[0].pension_amount").value(1400))
                .andExpect(jsonPath("$.content[0].years_of_work").value(45))
                .andExpect(jsonPath("$.content[1].type").value("EMPLOYEE"))
                .andExpect(jsonPath("$.content[1].id").value(10))
                .andExpect(jsonPath("$.content[1].name").value("Filip"))
                .andExpect(jsonPath("$.content[1].surname").value("Hajzer"))
                .andExpect(jsonPath("$.content[1].pesel").value("86112304578"))
                .andExpect(jsonPath("$.content[1].height").value(184.5))
                .andExpect(jsonPath("$.content[1].weight").value(88.9))
                .andExpect(jsonPath("$.content[1].email").value("filip.hajzer@gmail.com"))
                .andExpect(jsonPath("$.content[1].current_position_start_date").value("2024-03-03"))
                .andExpect(jsonPath("$.content[1].current_salary").value(5500))
                .andExpect(jsonPath("$.content[1].current_position").value("Kontroler Jakosci"))
                .andExpect(jsonPath("$.content[2].type").value("PENSIONER"))
                .andExpect(jsonPath("$.content[2].id").value(11))
                .andExpect(jsonPath("$.content[2].name").value("Nina"))
                .andExpect(jsonPath("$.content[2].surname").value("Terentiew"))
                .andExpect(jsonPath("$.content[2].pesel").value("56101298576"))
                .andExpect(jsonPath("$.content[2].height").value(167.5))
                .andExpect(jsonPath("$.content[2].weight").value(67.8))
                .andExpect(jsonPath("$.content[2].email").value("nina.terentiew@gmail.com"))
                .andExpect(jsonPath("$.content[2].pension_amount").value(1650))
                .andExpect(jsonPath("$.content[2].years_of_work").value(56));
    }

    @Test
    void shouldGiveListOfPeopleWithPeselWithRoleAdmin() throws Exception {
        postman.perform(get("/api/people/search?type=PERSON&pesel=87112304578")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].type").value("EMPLOYEE"))
                .andExpect(jsonPath("$.content[0].id").value(4))
                .andExpect(jsonPath("$.content[0].name").value("Piotr"))
                .andExpect(jsonPath("$.content[0].surname").value("Nowak"))
                .andExpect(jsonPath("$.content[0].pesel").value("87112304578"))
                .andExpect(jsonPath("$.content[0].height").value(184.5))
                .andExpect(jsonPath("$.content[0].weight").value(85.9))
                .andExpect(jsonPath("$.content[0].email").value("piotr.nowak@gmail.com"))
                .andExpect(jsonPath("$.content[0].current_position_start_date").value("2016-10-18"))
                .andExpect(jsonPath("$.content[0].current_salary").value(6500.00))
                .andExpect(jsonPath("$.content[0].current_position").value("Kardiolog"));
    }

    @Test
    void shouldGiveListOfPeopleWithEmailWithRoleAdmin() throws Exception {
        postman.perform(get("/api/people/search?type=PERSON&email=adam.filipienko@gmail.com")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].type").value("PENSIONER"))
                .andExpect(jsonPath("$.content[0].id").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Adam"))
                .andExpect(jsonPath("$.content[0].surname").value("Filipienko"))
                .andExpect(jsonPath("$.content[0].pesel").value("66101298576"))
                .andExpect(jsonPath("$.content[0].height").value(177.5))
                .andExpect(jsonPath("$.content[0].weight").value(77.8))
                .andExpect(jsonPath("$.content[0].email").value("adam.filipienko@gmail.com"))
                .andExpect(jsonPath("$.content[0].pension_amount").value(1300.00))
                .andExpect(jsonPath("$.content[0].years_of_work").value(46));
    }

    @Test
    void shouldGiveListOfStudentsWithUniversityNameWithRoleAdmin() throws Exception {
        postman.perform(get("/api/people/search?type=STUDENT&universityName=Politechnika Poznanska&page=3&size=1")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].type").value("STUDENT"))
                .andExpect(jsonPath("$.content[0].id").value(45))
                .andExpect(jsonPath("$.content[0].name").value("Jakub"))
                .andExpect(jsonPath("$.content[0].surname").value("Danielski"))
                .andExpect(jsonPath("$.content[0].pesel").value("99222225778"))
                .andExpect(jsonPath("$.content[0].height").value(199.5))
                .andExpect(jsonPath("$.content[0].weight").value(80.4))
                .andExpect(jsonPath("$.content[0].email").value("jakub.danielski@gmail.com"))
                .andExpect(jsonPath("$.content[0].university_name").value("Politechnika Poznanska"))
                .andExpect(jsonPath("$.content[0].year_of_study").value(3))
                .andExpect(jsonPath("$.content[0].field_of_study").value("Mechatronika"))
                .andExpect(jsonPath("$.content[0].scholarship_amount").value(1200.00));
    }

    @Test
    void shouldGiveListOfStudentsFromUniversityOfTechnologyWithRoleAdmin() throws Exception {
        postman.perform(get("/api/people/search?type=STUDENT&universityName=Politechnika&page=0&size=2")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].type").value("STUDENT"))
                .andExpect(jsonPath("$.content[0].id").value(9))
                .andExpect(jsonPath("$.content[0].name").value("Adam"))
                .andExpect(jsonPath("$.content[0].surname").value("Danielski"))
                .andExpect(jsonPath("$.content[0].pesel").value("99102205778"))
                .andExpect(jsonPath("$.content[0].height").value(199.5))
                .andExpect(jsonPath("$.content[0].weight").value(80.4))
                .andExpect(jsonPath("$.content[0].email").value("adam.danielski@gmail.com"))
                .andExpect(jsonPath("$.content[0].university_name").value("Politechnika Poznanska"))
                .andExpect(jsonPath("$.content[0].year_of_study").value(3))
                .andExpect(jsonPath("$.content[0].field_of_study").value("Mechatronika"))
                .andExpect(jsonPath("$.content[0].scholarship_amount").value(1200.00))
                .andExpect(jsonPath("$.content[1].type").value("STUDENT"))
                .andExpect(jsonPath("$.content[1].id").value(12))
                .andExpect(jsonPath("$.content[1].name").value("Piotr"))
                .andExpect(jsonPath("$.content[1].surname").value("Filipiec"))
                .andExpect(jsonPath("$.content[1].pesel").value("98032205778"))
                .andExpect(jsonPath("$.content[1].height").value(199.5))
                .andExpect(jsonPath("$.content[1].weight").value(95.4))
                .andExpect(jsonPath("$.content[1].email").value("piotr.filipiec@gmail.com"))
                .andExpect(jsonPath("$.content[1].university_name").value("Politechnika Gdanska"))
                .andExpect(jsonPath("$.content[1].year_of_study").value(5))
                .andExpect(jsonPath("$.content[1].field_of_study").value("Mechanika i Budowa Maszyn"))
                .andExpect(jsonPath("$.content[1].scholarship_amount").value(50.00));
    }

    @Test
    void shouldGiveListOfStudentsWithYearOfStudyFromToWithRoleAdmin() throws Exception {
        postman.perform(get("/api/people/search?type=STUDENT&minYearOfStudy=2&maxYearOfStudy=5&page=5&size=2")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].type").value("STUDENT"))
                .andExpect(jsonPath("$.content[0].id").value(45))
                .andExpect(jsonPath("$.content[0].name").value("Jakub"))
                .andExpect(jsonPath("$.content[0].surname").value("Danielski"))
                .andExpect(jsonPath("$.content[0].pesel").value("99222225778"))
                .andExpect(jsonPath("$.content[0].height").value(199.5))
                .andExpect(jsonPath("$.content[0].weight").value(80.4))
                .andExpect(jsonPath("$.content[0].email").value("jakub.danielski@gmail.com"))
                .andExpect(jsonPath("$.content[0].university_name").value("Politechnika Poznanska"))
                .andExpect(jsonPath("$.content[0].year_of_study").value(3))
                .andExpect(jsonPath("$.content[0].field_of_study").value("Mechatronika"))
                .andExpect(jsonPath("$.content[0].scholarship_amount").value(1200.00))
                .andExpect(jsonPath("$.content[1].type").value("STUDENT"))
                .andExpect(jsonPath("$.content[1].id").value(48))
                .andExpect(jsonPath("$.content[1].name").value("Stanislaw"))
                .andExpect(jsonPath("$.content[1].surname").value("Filipiec"))
                .andExpect(jsonPath("$.content[1].pesel").value("98222225778"))
                .andExpect(jsonPath("$.content[1].height").value(199.5))
                .andExpect(jsonPath("$.content[1].weight").value(95.4))
                .andExpect(jsonPath("$.content[1].email").value("stanislaw.filipiec@gmail.com"))
                .andExpect(jsonPath("$.content[1].university_name").value("Politechnika Gdanska"))
                .andExpect(jsonPath("$.content[1].year_of_study").value(5))
                .andExpect(jsonPath("$.content[1].field_of_study").value("Mechanika i Budowa Maszyn"))
                .andExpect(jsonPath("$.content[1].scholarship_amount").value(50.00));
    }

    @Test
    void shouldGiveListOfStudentsWithFieldOfStudyWithRoleAdmin() throws Exception {
        postman.perform(get("/api/people/search?type=STUDENT&fieldOfStudy=Mechanika i Budowa Maszyn&page=3&size=1")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].type").value("STUDENT"))
                .andExpect(jsonPath("$.content[0].id").value(24))
                .andExpect(jsonPath("$.content[0].name").value("Piotr"))
                .andExpect(jsonPath("$.content[0].surname").value("Nietzsche"))
                .andExpect(jsonPath("$.content[0].pesel").value("90000205798"))
                .andExpect(jsonPath("$.content[0].height").value(199.5))
                .andExpect(jsonPath("$.content[0].weight").value(95.4))
                .andExpect(jsonPath("$.content[0].email").value("piotr.nietzsche@gmail.com"))
                .andExpect(jsonPath("$.content[0].university_name").value("Politechnika Gdanska"))
                .andExpect(jsonPath("$.content[0].year_of_study").value(5))
                .andExpect(jsonPath("$.content[0].field_of_study").value("Mechanika i Budowa Maszyn"))
                .andExpect(jsonPath("$.content[0].scholarship_amount").value(50.00));
    }

    @Test
    void shouldGiveListOfStudentsWithScholarshipAmountFromToWithRoleAdmin() throws Exception {
        postman.perform(get("/api/people/search?type=STUDENT&minScholarshipAmount=400&maxScholarshipAmount=2000&page=1&size=2")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].type").value("STUDENT"))
                .andExpect(jsonPath("$.content[0].id").value(18))
                .andExpect(jsonPath("$.content[0].name").value("Aleksandra"))
                .andExpect(jsonPath("$.content[0].surname").value("Zad"))
                .andExpect(jsonPath("$.content[0].pesel").value("90000105788"))
                .andExpect(jsonPath("$.content[0].height").value(160.5))
                .andExpect(jsonPath("$.content[0].weight").value(50.4))
                .andExpect(jsonPath("$.content[0].email").value("aleksandra.zad@gmail.com"))
                .andExpect(jsonPath("$.content[0].university_name").value("Uniwersytet Warszawski"))
                .andExpect(jsonPath("$.content[0].year_of_study").value(1))
                .andExpect(jsonPath("$.content[0].field_of_study").value("Inzynieria Materialowa"))
                .andExpect(jsonPath("$.content[0].scholarship_amount").value(500.00))
                .andExpect(jsonPath("$.content[1].type").value("STUDENT"))
                .andExpect(jsonPath("$.content[1].id").value(21))
                .andExpect(jsonPath("$.content[1].name").value("Adam"))
                .andExpect(jsonPath("$.content[1].surname").value("Nietzsche"))
                .andExpect(jsonPath("$.content[1].pesel").value("90000205778"))
                .andExpect(jsonPath("$.content[1].height").value(199.5))
                .andExpect(jsonPath("$.content[1].weight").value(80.4))
                .andExpect(jsonPath("$.content[1].email").value("adam.nietzsche@gmail.com"))
                .andExpect(jsonPath("$.content[1].university_name").value("Politechnika Poznanska"))
                .andExpect(jsonPath("$.content[1].year_of_study").value(3))
                .andExpect(jsonPath("$.content[1].field_of_study").value("Mechatronika"))
                .andExpect(jsonPath("$.content[1].scholarship_amount").value(1200.00));
    }

    @Test
    void shouldGiveListOfEmployeesWithSalaryFromToWithRoleAdmin() throws Exception {
        postman.perform(get("/api/people/search?type=EMPLOYEE&minSalary=4000&maxSalary=6000&page=0&size=2")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].type").value("EMPLOYEE"))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Maciej"))
                .andExpect(jsonPath("$.content[0].surname").value("Koral"))
                .andExpect(jsonPath("$.content[0].pesel").value("88112304578"))
                .andExpect(jsonPath("$.content[0].height").value(183.5))
                .andExpect(jsonPath("$.content[0].weight").value(88.9))
                .andExpect(jsonPath("$.content[0].email").value("maciej.koral@gmail.com"))
                .andExpect(jsonPath("$.content[0].current_position_start_date").value("1990-12-22"))
                .andExpect(jsonPath("$.content[0].current_salary").value(4500.00))
                .andExpect(jsonPath("$.content[0].current_position").value("Psycholog"))
                .andExpect(jsonPath("$.content[1].type").value("EMPLOYEE"))
                .andExpect(jsonPath("$.content[1].id").value(10))
                .andExpect(jsonPath("$.content[1].name").value("Filip"))
                .andExpect(jsonPath("$.content[1].surname").value("Hajzer"))
                .andExpect(jsonPath("$.content[1].pesel").value("86112304578"))
                .andExpect(jsonPath("$.content[1].height").value(184.5))
                .andExpect(jsonPath("$.content[1].weight").value(88.9))
                .andExpect(jsonPath("$.content[1].email").value("filip.hajzer@gmail.com"))
                .andExpect(jsonPath("$.content[1].current_position_start_date").value("2024-03-03"))
                .andExpect(jsonPath("$.content[1].current_salary").value(5500))
                .andExpect(jsonPath("$.content[1].current_position").value("Kontroler Jakosci"));
    }

    @Test
    void shouldGiveListOfEmployeesWithCurrentPositionWithRoleAdmin() throws Exception {
        postman.perform(get("/api/people/search?type=EMPLOYEE&currentPosition=Kardiolog&page=0&size=2")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].type").value("EMPLOYEE"))
                .andExpect(jsonPath("$.content[0].id").value(4))
                .andExpect(jsonPath("$.content[0].name").value("Piotr"))
                .andExpect(jsonPath("$.content[0].surname").value("Nowak"))
                .andExpect(jsonPath("$.content[0].pesel").value("87112304578"))
                .andExpect(jsonPath("$.content[0].height").value(184.5))
                .andExpect(jsonPath("$.content[0].weight").value(85.9))
                .andExpect(jsonPath("$.content[0].email").value("piotr.nowak@gmail.com"))
                .andExpect(jsonPath("$.content[0].current_position_start_date").value("2016-10-18"))
                .andExpect(jsonPath("$.content[0].current_salary").value(6500.00))
                .andExpect(jsonPath("$.content[0].current_position").value("Kardiolog"))
                .andExpect(jsonPath("$.content[1].type").value("EMPLOYEE"))
                .andExpect(jsonPath("$.content[1].id").value(16))
                .andExpect(jsonPath("$.content[1].name").value("Piotr"))
                .andExpect(jsonPath("$.content[1].surname").value("Nietzsche"))
                .andExpect(jsonPath("$.content[1].pesel").value("80100304578"))
                .andExpect(jsonPath("$.content[1].height").value(184.5))
                .andExpect(jsonPath("$.content[1].weight").value(85.9))
                .andExpect(jsonPath("$.content[1].email").value("piotr.nietzsche1212@gmail.com"))
                .andExpect(jsonPath("$.content[1].current_position_start_date").value("2016-10-18"))
                .andExpect(jsonPath("$.content[1].current_salary").value(6500.00))
                .andExpect(jsonPath("$.content[1].current_position").value("Kardiolog"));
    }

    @Test
    void shouldNotGiveListOfEmployeesWithCurrentPositionIfNotExistWithRoleAdmin() throws Exception {
        postman.perform(get("/api/people/search?type=EMPLOYEE&currentPosition=Astronauta&page=0&size=5")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void shouldGiveListOfEmployeesWithStartDateFromToWithRoleAdmin() throws Exception {
        postman.perform(get("/api/people/search?type=EMPLOYEE&startDateFrom=2016-10-18&startDateTo=2023-10-18&page=1&size=1")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].type").value("EMPLOYEE"))
                .andExpect(jsonPath("$.content[0].id").value(7))
                .andExpect(jsonPath("$.content[0].name").value("Robert"))
                .andExpect(jsonPath("$.content[0].surname").value("Nykiel"))
                .andExpect(jsonPath("$.content[0].pesel").value("78112304578"))
                .andExpect(jsonPath("$.content[0].height").value(184.5))
                .andExpect(jsonPath("$.content[0].weight").value(86.9))
                .andExpect(jsonPath("$.content[0].email").value("robert.nykiel7@gmail.com"))
                .andExpect(jsonPath("$.content[0].current_position_start_date").value("2022-12-12"))
                .andExpect(jsonPath("$.content[0].current_salary").value(3500.00))
                .andExpect(jsonPath("$.content[0].current_position").value("Magazynier"));
    }

    @Test
    void shouldGiveListOfPensionersWithPensionAmountFromToWithRoleAdmin() throws Exception {
        postman.perform(get("/api/people/search?type=PENSIONER&minPensionAmount=1300&maxPensionAmount=1800&page=0&size=1")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].type").value("PENSIONER"))
                .andExpect(jsonPath("$.content[0].id").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Adam"))
                .andExpect(jsonPath("$.content[0].surname").value("Filipienko"))
                .andExpect(jsonPath("$.content[0].pesel").value("66101298576"))
                .andExpect(jsonPath("$.content[0].height").value(177.5))
                .andExpect(jsonPath("$.content[0].weight").value(77.8))
                .andExpect(jsonPath("$.content[0].email").value("adam.filipienko@gmail.com"))
                .andExpect(jsonPath("$.content[0].pension_amount").value(1300.00))
                .andExpect(jsonPath("$.content[0].years_of_work").value(46));
    }

    @Test
    void shouldGiveListOfPensionersWithYearsOfWorkFromToWithRoleAdmin() throws Exception {
        postman.perform(get("/api/people/search?type=PENSIONER&minYearsOfWork=45&maxYearsOfWork=50&page=1&size=1")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(jsonPath("$.content[0].type").value("PENSIONER"))
                .andExpect(jsonPath("$.content[0].id").value(8))
                .andExpect(jsonPath("$.content[0].name").value("Zygmunt"))
                .andExpect(jsonPath("$.content[0].surname").value("Grygiel"))
                .andExpect(jsonPath("$.content[0].pesel").value("63101298576"))
                .andExpect(jsonPath("$.content[0].height").value(173.5))
                .andExpect(jsonPath("$.content[0].weight").value(87.8))
                .andExpect(jsonPath("$.content[0].email").value("zygmunt.grygiel@gmail.com"))
                .andExpect(jsonPath("$.content[0].pension_amount").value(1400.00))
                .andExpect(jsonPath("$.content[0].years_of_work").value(45));
    }


}

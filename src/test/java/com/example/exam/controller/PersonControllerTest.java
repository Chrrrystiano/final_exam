package com.example.exam.controller;

import com.example.exam.DatabaseCleaner;
import com.example.exam.ExamApplication;


import com.example.exam.payload.request.LoginRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import liquibase.exception.LiquibaseException;
import org.junit.jupiter.api.Test;


import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest(classes = ExamApplication.class)
@AutoConfigureMockMvc
@DirtiesContext
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
        String personJson = "{\n" +
                "  \"type\": \"STUDENT\",\n" +
                "  \"data\": {\n" +
                "    \"name\": \"Maciej\",\n" +
                "    \"surname\": \"Wisniewski\",\n" +
                "    \"pesel\": \"98010112345\",\n" +
                "    \"height\": 175.70,\n" +
                "    \"weight\": 70.80,\n" +
                "    \"email\": \"michal.wisniewski@gmail.com\",\n" +
                "    \"universityName\": \"Uniwersytet Warszawski\",\n" +
                "    \"yearOfStudy\": 2,\n" +
                "    \"fieldOfStudy\": \"Informatyka\",\n" +
                "    \"scholarshipAmount\": 1000.00\n" +
                "  }\n" +
                "}";

        postman.perform(get("/api/people/search?type=STUDENT&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        postman.perform(post("/api/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(personJson)
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
        String personJson = "{\n" +
                "  \"type\": \"STUDENT\",\n" +
                "  \"data\": {\n" +
                "    \"name\": \"Maciej\",\n" +
                "    \"surname\": \"Wisniewski\",\n" +
                "    \"pesel\": \"98010112345\",\n" +
                "    \"height\": 175.70,\n" +
                "    \"weight\": 70.80,\n" +
                "    \"email\": \"michal.wisniewski@gmail.com\",\n" +
                "    \"universityName\": \"Uniwersytet Warszawski\",\n" +
                "    \"yearOfStudy\": 2,\n" +
                "    \"fieldOfStudy\": \"Informatyka\",\n" +
                "    \"scholarshipAmount\": 1000.00\n" +
                "  }\n" +
                "}";

        postman.perform(post("/api/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(personJson)
                        .header("Authorization", VALID_USER_TOKEN))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotSaveStudentWithRoleImporter() throws Exception {
        String personJson = "{\n" +
                "  \"type\": \"STUDENT\",\n" +
                "  \"data\": {\n" +
                "    \"name\": \"Maciej\",\n" +
                "    \"surname\": \"Wisniewski\",\n" +
                "    \"pesel\": \"98010112345\",\n" +
                "    \"height\": 175.70,\n" +
                "    \"weight\": 70.80,\n" +
                "    \"email\": \"michal.wisniewski@gmail.com\",\n" +
                "    \"universityName\": \"Uniwersytet Warszawski\",\n" +
                "    \"yearOfStudy\": 2,\n" +
                "    \"fieldOfStudy\": \"Informatyka\",\n" +
                "    \"scholarshipAmount\": 1000.00\n" +
                "  }\n" +
                "}";

        postman.perform(post("/api/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(personJson)
                        .header("Authorization", VALID_IMPORTER_TOKEN))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotSaveStudentWithInvalidToken() throws Exception {
        String personJson = "{\n" +
                "  \"type\": \"STUDENT\",\n" +
                "  \"data\": {\n" +
                "    \"name\": \"Maciej\",\n" +
                "    \"surname\": \"Wisniewski\",\n" +
                "    \"pesel\": \"98010112345\",\n" +
                "    \"height\": 175.70,\n" +
                "    \"weight\": 70.80,\n" +
                "    \"email\": \"michal.wisniewski@gmail.com\",\n" +
                "    \"universityName\": \"Uniwersytet Warszawski\",\n" +
                "    \"yearOfStudy\": 2,\n" +
                "    \"fieldOfStudy\": \"Informatyka\",\n" +
                "    \"scholarshipAmount\": 1000.00\n" +
                "  }\n" +
                "}";

        postman.perform(post("/api/people/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(personJson)
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
        String personJson = "{\n" +
                "  \"type\": \"STUDENT\",\n" +
                "  \"data\": {\n" +
                "    \"name\": \"Maciej\",\n" +
                "    \"surname\": \"Wisniewski\",\n" +
                "    \"pesel\": \"98010112345\",\n" +
                "    \"height\": 175.70,\n" +
                "    \"weight\": 70.80,\n" +
                "    \"email\": \"michal.wisniewski@gmail.com\",\n" +
                "    \"universityName\": \"Uniwersytet Warszawski\",\n" +
                "    \"yearOfStudy\": 2,\n" +
                "    \"fieldOfStudy\": \"Informatyka\",\n" +
                "    \"scholarshipAmount\": 1000.00\n" +
                "  }\n" +
                "}";

        postman.perform(post("/api/people/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(personJson))
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
        String personJson = "{\n" +
                "  \"type\": \"STUDENT\",\n" +
                "  \"data\": {\n" +
                "    \"name\": \"\",\n" +
                "    \"surname\": \"Wisniewski\",\n" +
                "    \"pesel\": \"98010112345\",\n" +
                "    \"height\": 175.70,\n" +
                "    \"weight\": 70.80,\n" +
                "    \"email\": \"michal.wisniewski@gmail.com\",\n" +
                "    \"universityName\": \"Uniwersytet Warszawski\",\n" +
                "    \"yearOfStudy\": 2,\n" +
                "    \"fieldOfStudy\": \"Informatyka\",\n" +
                "    \"scholarshipAmount\": 1000.00\n" +
                "  }\n" +
                "}";

        postman.perform(get("/api/people/search?type=STUDENT&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(personJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("name: The NAME field cannot be left empty"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotSaveStudentWhenSurnameIsBlankWithRoleAdmin() throws Exception {
        String personJson = "{\n" +
                "  \"type\": \"STUDENT\",\n" +
                "  \"data\": {\n" +
                "    \"name\": \"Ada\",\n" +
                "    \"surname\": \"\",\n" +
                "    \"pesel\": \"98010112345\",\n" +
                "    \"height\": 175.70,\n" +
                "    \"weight\": 70.80,\n" +
                "    \"email\": \"michal.wisniewski@gmail.com\",\n" +
                "    \"universityName\": \"Uniwersytet Warszawski\",\n" +
                "    \"yearOfStudy\": 2,\n" +
                "    \"fieldOfStudy\": \"Informatyka\",\n" +
                "    \"scholarshipAmount\": 1000.00\n" +
                "  }\n" +
                "}";

        postman.perform(get("/api/people/search?type=STUDENT&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(personJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("surname: The SURNAME field cannot be left empty"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotSaveStudentWhenPeselIsShorterWithRoleAdmin() throws Exception {
        String personJson = "{\n" +
                "  \"type\": \"STUDENT\",\n" +
                "  \"data\": {\n" +
                "    \"name\": \"Ada\",\n" +
                "    \"surname\": \"Trytko\",\n" +
                "    \"pesel\": \"3534\",\n" +
                "    \"height\": 175.70,\n" +
                "    \"weight\": 70.80,\n" +
                "    \"email\": \"michal.wisniewski@gmail.com\",\n" +
                "    \"universityName\": \"Uniwersytet Warszawski\",\n" +
                "    \"yearOfStudy\": 2,\n" +
                "    \"fieldOfStudy\": \"Informatyka\",\n" +
                "    \"scholarshipAmount\": 1000.00\n" +
                "  }\n" +
                "}";

        postman.perform(get("/api/people/search?type=STUDENT&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(personJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("pesel: Pesel must have exactly 11 digits!"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotSaveStudentWhenPeselIsLongerWithRoleAdmin() throws Exception {
        String personJson = "{\n" +
                "  \"type\": \"STUDENT\",\n" +
                "  \"data\": {\n" +
                "    \"name\": \"Ada\",\n" +
                "    \"surname\": \"Trytko\",\n" +
                "    \"pesel\": \"3521354554543543534534534\",\n" +
                "    \"height\": 175.70,\n" +
                "    \"weight\": 70.80,\n" +
                "    \"email\": \"michal.wisniewski@gmail.com\",\n" +
                "    \"universityName\": \"Uniwersytet Warszawski\",\n" +
                "    \"yearOfStudy\": 2,\n" +
                "    \"fieldOfStudy\": \"Informatyka\",\n" +
                "    \"scholarshipAmount\": 1000.00\n" +
                "  }\n" +
                "}";

        postman.perform(get("/api/people/search?type=STUDENT&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(personJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("pesel: Pesel must have exactly 11 digits!"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotSaveStudentWhenPeselAlreadyExistsWithRoleAdmin() throws Exception {
        String personJson = "{\n" +
                "  \"type\": \"STUDENT\",\n" +
                "  \"data\": {\n" +
                "    \"name\": \"Ada\",\n" +
                "    \"surname\": \"Trytko\",\n" +
                "    \"pesel\": \"66111118586\",\n" +
                "    \"height\": 175.70,\n" +
                "    \"weight\": 70.80,\n" +
                "    \"email\": \"michal.wisniewski@gmail.com\",\n" +
                "    \"universityName\": \"Uniwersytet Warszawski\",\n" +
                "    \"yearOfStudy\": 2,\n" +
                "    \"fieldOfStudy\": \"Informatyka\",\n" +
                "    \"scholarshipAmount\": 1000.00\n" +
                "  }\n" +
                "}";

        postman.perform(get("/api/people/search?type=STUDENT&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(personJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("This pesel number is already assigned to the user in the database."))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotSaveStudentWhenEmailAlreadyExistsWithRoleAdmin() throws Exception {
        String personJson = "{\n" +
                "  \"type\": \"STUDENT\",\n" +
                "  \"data\": {\n" +
                "    \"name\": \"Ada\",\n" +
                "    \"surname\": \"Trytko\",\n" +
                "    \"pesel\": \"99991118586\",\n" +
                "    \"height\": 175.70,\n" +
                "    \"weight\": 70.80,\n" +
                "    \"email\": \"piotr.nietzsche1212@gmail.com\",\n" +
                "    \"universityName\": \"Uniwersytet Warszawski\",\n" +
                "    \"yearOfStudy\": 2,\n" +
                "    \"fieldOfStudy\": \"Informatyka\",\n" +
                "    \"scholarshipAmount\": 1000.00\n" +
                "  }\n" +
                "}";

        postman.perform(get("/api/people/search?type=STUDENT&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(personJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("This email address is already assigned to the user in the database"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotSaveStudentWhenHeightIsTooBigWithRoleAdmin() throws Exception {
        String personJson = "{\n" +
                "  \"type\": \"STUDENT\",\n" +
                "  \"data\": {\n" +
                "    \"name\": \"Ada\",\n" +
                "    \"surname\": \"Trytko\",\n" +
                "    \"pesel\": \"98565656565\",\n" +
                "    \"height\": 234234,\n" +
                "    \"weight\": 70.80,\n" +
                "    \"email\": \"michal.wisniewski@gmail.com\",\n" +
                "    \"universityName\": \"Uniwersytet Warszawski\",\n" +
                "    \"yearOfStudy\": 2,\n" +
                "    \"fieldOfStudy\": \"Informatyka\",\n" +
                "    \"scholarshipAmount\": 1000.00\n" +
                "  }\n" +
                "}";

        postman.perform(get("/api/people/search?type=STUDENT&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(personJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("height: Height must be less than 300"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotSaveStudentWhenHeightIsTooSmallWithRoleAdmin() throws Exception {
        String personJson = "{\n" +
                "  \"type\": \"STUDENT\",\n" +
                "  \"data\": {\n" +
                "    \"name\": \"Ada\",\n" +
                "    \"surname\": \"Trytko\",\n" +
                "    \"pesel\": \"98565656565\",\n" +
                "    \"height\": -234234,\n" +
                "    \"weight\": 70.80,\n" +
                "    \"email\": \"michal.wisniewski@gmail.com\",\n" +
                "    \"universityName\": \"Uniwersytet Warszawski\",\n" +
                "    \"yearOfStudy\": 2,\n" +
                "    \"fieldOfStudy\": \"Informatyka\",\n" +
                "    \"scholarshipAmount\": 1000.00\n" +
                "  }\n" +
                "}";

        postman.perform(get("/api/people/search?type=STUDENT&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(personJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("height: Height must be greater than 0"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotSaveStudentWhenWeightIsTooBigWithRoleAdmin() throws Exception {
        String personJson = "{\n" +
                "  \"type\": \"STUDENT\",\n" +
                "  \"data\": {\n" +
                "    \"name\": \"Ada\",\n" +
                "    \"surname\": \"Trytko\",\n" +
                "    \"pesel\": \"98565656565\",\n" +
                "    \"height\": 200.19,\n" +
                "    \"weight\": 700,\n" +
                "    \"email\": \"michal.wisniewski@gmail.com\",\n" +
                "    \"universityName\": \"Uniwersytet Warszawski\",\n" +
                "    \"yearOfStudy\": 2,\n" +
                "    \"fieldOfStudy\": \"Informatyka\",\n" +
                "    \"scholarshipAmount\": 1000.00\n" +
                "  }\n" +
                "}";

        postman.perform(get("/api/people/search?type=STUDENT&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(personJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("weight: Weight must be less than 300"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotSaveStudentWhenWeightIsTooSmallWithRoleAdmin() throws Exception {
        String personJson = "{\n" +
                "  \"type\": \"STUDENT\",\n" +
                "  \"data\": {\n" +
                "    \"name\": \"Ada\",\n" +
                "    \"surname\": \"Trytko\",\n" +
                "    \"pesel\": \"98565656565\",\n" +
                "    \"height\": 198.65,\n" +
                "    \"weight\": -13,\n" +
                "    \"email\": \"michal.wisniewski@gmail.com\",\n" +
                "    \"universityName\": \"Uniwersytet Warszawski\",\n" +
                "    \"yearOfStudy\": 2,\n" +
                "    \"fieldOfStudy\": \"Informatyka\",\n" +
                "    \"scholarshipAmount\": 1000.00\n" +
                "  }\n" +
                "}";

        postman.perform(get("/api/people/search?type=STUDENT&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(personJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("weight: Weight must be greater than 0"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotSaveStudentWhenEmailIsEmptyWithRoleAdmin() throws Exception {
        String personJson = "{\n" +
                "  \"type\": \"STUDENT\",\n" +
                "  \"data\": {\n" +
                "    \"name\": \"Ada\",\n" +
                "    \"surname\": \"Trytko\",\n" +
                "    \"pesel\": \"98565656565\",\n" +
                "    \"height\": 198.65,\n" +
                "    \"weight\": 77.23,\n" +
                "    \"email\": \"\",\n" +
                "    \"universityName\": \"Uniwersytet Warszawski\",\n" +
                "    \"yearOfStudy\": 2,\n" +
                "    \"fieldOfStudy\": \"Informatyka\",\n" +
                "    \"scholarshipAmount\": 1000.00\n" +
                "  }\n" +
                "}";

        postman.perform(get("/api/people/search?type=STUDENT&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(personJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("email: The EMAIL field cannot be left empty"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotSaveStudentWhenEmailIsNotValidWithRoleAdmin() throws Exception {
        String personJson = "{\n" +
                "  \"type\": \"STUDENT\",\n" +
                "  \"data\": {\n" +
                "    \"name\": \"Ada\",\n" +
                "    \"surname\": \"Trytko\",\n" +
                "    \"pesel\": \"98565656565\",\n" +
                "    \"height\": 198.65,\n" +
                "    \"weight\": 77.23,\n" +
                "    \"email\": \"2121\",\n" +
                "    \"universityName\": \"Uniwersytet Warszawski\",\n" +
                "    \"yearOfStudy\": 2,\n" +
                "    \"fieldOfStudy\": \"Informatyka\",\n" +
                "    \"scholarshipAmount\": 1000.00\n" +
                "  }\n" +
                "}";

        postman.perform(get("/api/people/search?type=STUDENT&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(personJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("email: Email must be valid"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotSaveStudentWhenUniversityNameIsEmptyWithRoleAdmin() throws Exception {
        String personJson = "{\n" +
                "  \"type\": \"STUDENT\",\n" +
                "  \"data\": {\n" +
                "    \"name\": \"Ada\",\n" +
                "    \"surname\": \"Trytko\",\n" +
                "    \"pesel\": \"98565656565\",\n" +
                "    \"height\": 198.65,\n" +
                "    \"weight\": 77.23,\n" +
                "    \"email\": \"michal.wisniewski@gmail.com\",\n" +
                "    \"universityName\": \"\",\n" +
                "    \"yearOfStudy\": 2,\n" +
                "    \"fieldOfStudy\": \"Informatyka\",\n" +
                "    \"scholarshipAmount\": 1000.00\n" +
                "  }\n" +
                "}";

        postman.perform(get("/api/people/search?type=STUDENT&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(personJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("universityName: The UNIVERSITY field cannot be left empty"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotSaveStudentWhenYearOfStudyIsLessThan1WithRoleAdmin() throws Exception {
        String personJson = "{\n" +
                "  \"type\": \"STUDENT\",\n" +
                "  \"data\": {\n" +
                "    \"name\": \"Ada\",\n" +
                "    \"surname\": \"Trytko\",\n" +
                "    \"pesel\": \"98565656565\",\n" +
                "    \"height\": 198.65,\n" +
                "    \"weight\": 77.23,\n" +
                "    \"email\": \"michal.wisniewski@gmail.com\",\n" +
                "    \"universityName\": \"UJ\",\n" +
                "    \"yearOfStudy\": 0,\n" +
                "    \"fieldOfStudy\": \"Informatyka\",\n" +
                "    \"scholarshipAmount\": 1000.00\n" +
                "  }\n" +
                "}";

        postman.perform(get("/api/people/search?type=STUDENT&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(personJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("yearOfStudy: Year of study must be at least 1"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotSaveStudentWhenYearOfStudyIsGreaterThan1WithRoleAdmin() throws Exception {
        String personJson = "{\n" +
                "  \"type\": \"STUDENT\",\n" +
                "  \"data\": {\n" +
                "    \"name\": \"Ada\",\n" +
                "    \"surname\": \"Trytko\",\n" +
                "    \"pesel\": \"98565656565\",\n" +
                "    \"height\": 198.65,\n" +
                "    \"weight\": 77.23,\n" +
                "    \"email\": \"michal.wisniewski@gmail.com\",\n" +
                "    \"universityName\": \"UJ\",\n" +
                "    \"yearOfStudy\": 10,\n" +
                "    \"fieldOfStudy\": \"Informatyka\",\n" +
                "    \"scholarshipAmount\": 1000.00\n" +
                "  }\n" +
                "}";

        postman.perform(get("/api/people/search?type=STUDENT&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(personJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("yearOfStudy: Year of study must be no more than 5"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotSaveStudentWhenFieldOfStudyIsEmptyWithRoleAdmin() throws Exception {
        String personJson = "{\n" +
                "  \"type\": \"STUDENT\",\n" +
                "  \"data\": {\n" +
                "    \"name\": \"Ada\",\n" +
                "    \"surname\": \"Trytko\",\n" +
                "    \"pesel\": \"98565656565\",\n" +
                "    \"height\": 198.65,\n" +
                "    \"weight\": 77.23,\n" +
                "    \"email\": \"michal.wisniewski@gmail.com\",\n" +
                "    \"universityName\": \"UJ\",\n" +
                "    \"yearOfStudy\": 2,\n" +
                "    \"fieldOfStudy\": \"\",\n" +
                "    \"scholarshipAmount\": 1000.00\n" +
                "  }\n" +
                "}";

        postman.perform(get("/api/people/search?type=STUDENT&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(personJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("fieldOfStudy: The FIELD OF STUDY field cannot be left empty"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotSaveStudentWhenScholarshipAmountIsNegativeWithRoleAdmin() throws Exception {
        String personJson = "{\n" +
                "  \"type\": \"STUDENT\",\n" +
                "  \"data\": {\n" +
                "    \"name\": \"Ada\",\n" +
                "    \"surname\": \"Trytko\",\n" +
                "    \"pesel\": \"98565656565\",\n" +
                "    \"height\": 198.65,\n" +
                "    \"weight\": 77.23,\n" +
                "    \"email\": \"michal.wisniewski@gmail.com\",\n" +
                "    \"universityName\": \"UJ\",\n" +
                "    \"yearOfStudy\": 2,\n" +
                "    \"fieldOfStudy\": \"Mechanika\",\n" +
                "    \"scholarshipAmount\": -1000.00\n" +
                "  }\n" +
                "}";

        postman.perform(get("/api/people/search?type=STUDENT&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(personJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("scholarshipAmount: Scholarship amount must be positive or zero"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldEditStudentWithAdminRole() throws Exception {
        String personJson = "{\n" +
                "  \"type\": \"STUDENT\",\n" +
                "  \"data\": {\n" +
                "    \"id\": 3,\n" +
                "    \"name\": \"XXXXXPiotr\",\n" +
                "    \"surname\": \"XXXXXFilipiec\",\n" +
                "    \"pesel\": \"98032205778\",\n" +
                "    \"height\": 199.5,\n" +
                "    \"weight\": 95.4,\n" +
                "    \"universityName\": \"Politechnika Gdanska\",\n" +
                "    \"yearOfStudy\": 5,\n" +
                "    \"fieldOfStudy\": \"Mechanika\",\n" +
                "    \"scholarshipAmount\": 50\n" +
                "  }\n" +
                "}";

        postman.perform(put("/api/people/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(personJson)
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("XXXXXPiotr"))
                .andExpect(jsonPath("$.surname").value("XXXXXFilipiec"));
    }

    @Test
    void shouldNotEditStudentWithoutNameAdminRole() throws Exception {
        String personJson = "{\n" +
                "  \"type\": \"STUDENT\",\n" +
                "  \"data\": {\n" +
                "    \"id\": 3,\n" +
                "    \"name\": \"\",\n" +
                "    \"surname\": \"XXXXXFilipiec\",\n" +
                "    \"pesel\": \"98032205778\",\n" +
                "    \"height\": 199.5,\n" +
                "    \"weight\": 95.4,\n" +
                "    \"email\": \"piotr.filipiec@gmail.com\",\n" +
                "    \"universityName\": \"Politechnika Gdanska\",\n" +
                "    \"yearOfStudy\": 5,\n" +
                "    \"fieldOfStudy\": \"Mechanika\",\n" +
                "    \"scholarshipAmount\": 50\n" +
                "  }\n" +
                "}";

        postman.perform(put("/api/people/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(personJson)
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("name: The NAME field cannot be left empty"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotEditStudentWithInvalidToken() throws Exception {
        String personJson = "{\n" +
                "  \"type\": \"STUDENT\",\n" +
                "  \"data\": {\n" +
                "    \"id\": 51\n" +
                "    \"name\": \"XXXXXPiotr\",\n" +
                "    \"surname\": \"XXXXXFilipiec\",\n" +
                "    \"pesel\": \"98032205778\",\n" +
                "    \"height\": 199.5,\n" +
                "    \"weight\": 95.4,\n" +
                "    \"email\": \"piotr.filipiec@gmail.com\",\n" +
                "    \"universityName\": \"Politechnika Gdanska\",\n" +
                "    \"yearOfStudy\": 5,\n" +
                "    \"fieldOfStudy\": \"Mechanika\",\n" +
                "    \"scholarshipAmount\": 50\n" +
                "  }\n" +
                "}";

        postman.perform(put("/api/people/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(personJson)
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
        String personJson = "{\n" +
                "  \"type\": \"STUDENT\",\n" +
                "  \"data\": {\n" +
                "    \"id\": 51\n" +
                "    \"name\": \"XXXXXPiotr\",\n" +
                "    \"surname\": \"XXXXXFilipiec\",\n" +
                "    \"pesel\": \"98032205778\",\n" +
                "    \"height\": 199.5,\n" +
                "    \"weight\": 95.4,\n" +
                "    \"email\": \"piotr.filipiec@gmail.com\",\n" +
                "    \"universityName\": \"Politechnika Gdanska\",\n" +
                "    \"yearOfStudy\": 5,\n" +
                "    \"fieldOfStudy\": \"Mechanika\",\n" +
                "    \"scholarshipAmount\": 50\n" +
                "  }\n" +
                "}";

        postman.perform(post("/api/people/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(personJson))
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
        String personJson = "{\n" +
                "  \"type\": \"STUDENT\",\n" +
                "  \"data\": {\n" +
                "    \"id\": 12,\n" +
                "    \"name\": \"XXXXXPiotr\",\n" +
                "    \"surname\": \"XXXXXFilipiec\",\n" +
                "    \"pesel\": \"98032205778\",\n" +
                "    \"height\": 199.5,\n" +
                "    \"weight\": 95.4,\n" +
                "    \"email\": \"piotr.filipiec@gmail.com\",\n" +
                "    \"universityName\": \"Politechnika Gdanska\",\n" +
                "    \"yearOfStudy\": 5,\n" +
                "    \"fieldOfStudy\": \"Mechanika\",\n" +
                "    \"scholarshipAmount\": 50\n" +
                "  }\n" +
                "}";

        postman.perform(put("/api/people/12")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(personJson)
                        .header("Authorization", VALID_USER_TOKEN))
                .andDo(print())
                .andExpect(status().isForbidden());

    }

    @Test
    void shouldNotEditStudentWithRoleImporter() throws Exception {
        String personJson = "{\n" +
                "  \"type\": \"STUDENT\",\n" +
                "  \"data\": {\n" +
                "    \"id\": 51,\n" +
                "    \"name\": \"XXXXXPiotr\",\n" +
                "    \"surname\": \"XXXXXFilipiec\",\n" +
                "    \"pesel\": \"98032205778\",\n" +
                "    \"height\": 199.5,\n" +
                "    \"weight\": 95.4,\n" +
                "    \"email\": \"piotr.filipiec@gmail.com\",\n" +
                "    \"universityName\": \"Politechnika Gdanska\",\n" +
                "    \"yearOfStudy\": 5,\n" +
                "    \"fieldOfStudy\": \"Mechanika\",\n" +
                "    \"scholarshipAmount\": 50\n" +
                "  }\n" +
                "}";

        postman.perform(put("/api/people/51")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(personJson)
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


    @Test
    void shouldProcessFileUploadSuccessfullyWithRoleAdmin() throws Exception {
        ClassPathResource resource = new ClassPathResource("changesets/data/correct_people.csv");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "correct_people.csv",
                "text/csv",
                resource.getInputStream());

        MvcResult result = postman.perform(multipart("/api/people/imports").file(file)
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("The import has been accepted"))
                .andExpect(jsonPath("$.taskId", notNullValue()))
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        String taskId = JsonPath.read(jsonResponse, "$.taskId");

        Thread.sleep(1500);

        postman.perform(get("/api/import/" + taskId + "/status")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.taskId").value(taskId))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.startTime").value(notNullValue()))
                .andExpect(jsonPath("$.endTime").value(notNullValue()))
                .andExpect(jsonPath("$.processedRows").value(3));
    }

    @Test
    void shouldProcessFileUploadSuccessfullyWithRoleImporter() throws Exception {
        ClassPathResource resource = new ClassPathResource("changesets/data/correct_people.csv");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "correct_people.csv",
                "text/csv",
                resource.getInputStream());

        MvcResult result = postman.perform(multipart("/api/people/imports").file(file)
                        .header("Authorization", VALID_IMPORTER_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("The import has been accepted"))
                .andExpect(jsonPath("$.taskId", notNullValue()))
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        String taskId = JsonPath.read(jsonResponse, "$.taskId");

        Thread.sleep(1500);

        postman.perform(get("/api/import/" + taskId + "/status")
                        .header("Authorization", VALID_IMPORTER_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.taskId").value(taskId))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.startTime").value(notNullValue()))
                .andExpect(jsonPath("$.endTime").value(notNullValue()))
                .andExpect(jsonPath("$.processedRows").value(3));
    }

    @Test
    void shouldNotProcessFileUploadSuccessfullyWithRoleUser() throws Exception {
        ClassPathResource resource = new ClassPathResource("changesets/data/correct_people.csv");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "correct_people.csv",
                "text/csv",
                resource.getInputStream());

        postman.perform(multipart("/api/people/imports").file(file)
                        .header("Authorization", VALID_USER_TOKEN))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotProcessFileUploadSuccessfullyWithoutAutorization() throws Exception {
        ClassPathResource resource = new ClassPathResource("changesets/data/correct_people.csv");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "correct_people.csv",
                "text/csv",
                resource.getInputStream());

        postman.perform(multipart("/api/people/import").file(file))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.status").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Full authentication is required to access this resource"))
                .andExpect(jsonPath("$.uri").value("/api/people/import"))
                .andExpect(jsonPath("$.method").value("POST"));
    }

    @Test
    void shouldNotProcessFileUploadSuccessfullyWithInvalidToken() throws Exception {
        ClassPathResource resource = new ClassPathResource("changesets/data/correct_people.csv");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "correct_people.csv",
                "text/csv",
                resource.getInputStream());

        postman.perform(multipart("/api/people/import").file(file)
                        .header("Authorization", INVALID_TOKEN))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.status").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Full authentication is required to access this resource"))
                .andExpect(jsonPath("$.uri").value("/api/people/import"))
                .andExpect(jsonPath("$.method").value("POST"));
    }

    @Test
    void shouldNotProcessFileUploadWhenPeselAlreadyExistsWithRoleImporter() throws Exception {
        ClassPathResource resource = new ClassPathResource("changesets/data/duplicate_pesel.csv");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "duplicate_pesel.csv",
                "text/csv",
                resource.getInputStream());

        MvcResult result = postman.perform(multipart("/api/people/imports").file(file)
                        .header("Authorization", VALID_IMPORTER_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("The import has been accepted"))
                .andExpect(jsonPath("$.taskId", notNullValue()))
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        String taskId = JsonPath.read(jsonResponse, "$.taskId");

        Thread.sleep(1500);

        postman.perform(get("/api/import/" + taskId + "/status")
                        .header("Authorization", VALID_IMPORTER_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.taskId").value(taskId))
                .andExpect(jsonPath("$.status").value("REJECTED"))
                .andExpect(jsonPath("$.startTime").value(notNullValue()))
                .andExpect(jsonPath("$.endTime").value(notNullValue()))
                .andExpect(jsonPath("$.processedRows").value(0));
    }

    @Test
    void shouldNotProcessFileUploadWhenEmailAlreadyExistsWithRoleImporter() throws Exception {
        ClassPathResource resource = new ClassPathResource("changesets/data/duplicate_email.csv");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "duplicate_email.csv",
                "text/csv",
                resource.getInputStream());

        MvcResult result = postman.perform(multipart("/api/people/imports").file(file)
                        .header("Authorization", VALID_IMPORTER_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("The import has been accepted"))
                .andExpect(jsonPath("$.taskId", notNullValue()))
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        String taskId = JsonPath.read(jsonResponse, "$.taskId");

        Thread.sleep(1500);

        postman.perform(get("/api/import/" + taskId + "/status")
                        .header("Authorization", VALID_IMPORTER_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.taskId").value(taskId))
                .andExpect(jsonPath("$.status").value("REJECTED"))
                .andExpect(jsonPath("$.startTime").value(notNullValue()))
                .andExpect(jsonPath("$.endTime").value(notNullValue()))
                .andExpect(jsonPath("$.processedRows").value(0));
    }

    @Test
    void shouldNotProcessFileUploadWhenThePersonTypeIsNotSupportedWithRoleImporter() throws Exception {
        ClassPathResource resource = new ClassPathResource("changesets/data/unsupported_person_type.csv");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "unsupported_person_type.csv",
                "text/csv",
                resource.getInputStream());

        MvcResult result = postman.perform(multipart("/api/people/imports").file(file)
                        .header("Authorization", VALID_IMPORTER_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("The import has been accepted"))
                .andExpect(jsonPath("$.taskId", notNullValue()))
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        String taskId = JsonPath.read(jsonResponse, "$.taskId");

        Thread.sleep(1500);

        postman.perform(get("/api/import/" + taskId + "/status")
                        .header("Authorization", VALID_IMPORTER_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.taskId").value(taskId))
                .andExpect(jsonPath("$.status").value("REJECTED"))
                .andExpect(jsonPath("$.startTime").value(notNullValue()))
                .andExpect(jsonPath("$.endTime").value(notNullValue()))
                .andExpect(jsonPath("$.processedRows").value(0));
    }

    @Test
    void shouldNotProcessFileUploadWhenFileFormatIsIncorrectRoleImporter() throws Exception {
        ClassPathResource resource = new ClassPathResource("changesets/data/binary_file.bin");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "binary_file.bin",
                "application/octet-stream",
                resource.getInputStream());

        postman.perform(multipart("/api/people/imports").file(file)
                        .header("Authorization", VALID_IMPORTER_TOKEN))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Unsupported file type: application/octet-stream"));
    }


}

package com.example.exam.controller;

import com.example.exam.DatabaseCleaner;
import com.example.exam.ExamApplication;
import com.example.exam.payload.request.LoginRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import liquibase.exception.LiquibaseException;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(classes = ExamApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class ImportFileControllerTest {
    private final MockMvc postman;
    private final ObjectMapper objectMapper;
    private final DatabaseCleaner databaseCleaner;
    private final String VALID_USER_TOKEN;
    private final String VALID_ADMIN_TOKEN;
    private final String VALID_IMPORTER_TOKEN;
    private final String INVALID_TOKEN;

    @Autowired
    public ImportFileControllerTest(MockMvc postman, ObjectMapper objectMapper, DatabaseCleaner databaseCleaner) throws Exception {
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




//    @Test
//    void shouldProcessFileUploadSuccessfullyWithRoleAdmin() throws Exception {
//        ClassPathResource resource = new ClassPathResource("changesets/data/correct_people.csv");
//        MockMultipartFile file = new MockMultipartFile(
//                "file",
//                "correct_people.csv",
//                "text/csv",
//                resource.getInputStream());
//
//        MvcResult result = postman.perform(multipart("/api/people/imports").file(file)
//                        .header("Authorization", VALID_ADMIN_TOKEN))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("The import has been accepted"))
//                .andExpect(jsonPath("$.taskId", notNullValue()))
//                .andReturn();
//
//        String jsonResponse = result.getResponse().getContentAsString();
//        String taskId = JsonPath.read(jsonResponse, "$.taskId");
//
//        Thread.sleep(1500);
//
//        postman.perform(get("/api/import/" + taskId + "/status")
//                        .header("Authorization", VALID_ADMIN_TOKEN))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", notNullValue()))
//                .andExpect(jsonPath("$.taskId").value(taskId))
//                .andExpect(jsonPath("$.status").value("COMPLETED"))
//                .andExpect(jsonPath("$.startTime").value(notNullValue()))
//                .andExpect(jsonPath("$.endTime").value(notNullValue()))
//                .andExpect(jsonPath("$.processedRows").value(3));
//    }
//
//    @Test
//    void shouldProcessFileUploadSuccessfullyWithRoleImporter() throws Exception {
//        ClassPathResource resource = new ClassPathResource("changesets/data/correct_people.csv");
//        MockMultipartFile file = new MockMultipartFile(
//                "file",
//                "correct_people.csv",
//                "text/csv",
//                resource.getInputStream());
//
//        MvcResult result = postman.perform(multipart("/api/people/imports").file(file)
//                        .header("Authorization", VALID_IMPORTER_TOKEN))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("The import has been accepted"))
//                .andExpect(jsonPath("$.taskId", notNullValue()))
//                .andReturn();
//
//        String jsonResponse = result.getResponse().getContentAsString();
//        String taskId = JsonPath.read(jsonResponse, "$.taskId");
//
//        Thread.sleep(1500);
//
//        postman.perform(get("/api/import/" + taskId + "/status")
//                        .header("Authorization", VALID_IMPORTER_TOKEN))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", notNullValue()))
//                .andExpect(jsonPath("$.taskId").value(taskId))
//                .andExpect(jsonPath("$.status").value("COMPLETED"))
//                .andExpect(jsonPath("$.startTime").value(notNullValue()))
//                .andExpect(jsonPath("$.endTime").value(notNullValue()))
//                .andExpect(jsonPath("$.processedRows").value(3));
//    }
//
//    @Test
//    void shouldNotProcessFileUploadSuccessfullyWithRoleUser() throws Exception {
//        ClassPathResource resource = new ClassPathResource("changesets/data/correct_people.csv");
//        MockMultipartFile file = new MockMultipartFile(
//                "file",
//                "correct_people.csv",
//                "text/csv",
//                resource.getInputStream());
//
//        postman.perform(multipart("/api/people/imports").file(file)
//                        .header("Authorization", VALID_USER_TOKEN))
//                .andDo(print())
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    void shouldNotProcessFileUploadSuccessfullyWithoutAutorization() throws Exception {
//        ClassPathResource resource = new ClassPathResource("changesets/data/correct_people.csv");
//        MockMultipartFile file = new MockMultipartFile(
//                "file",
//                "correct_people.csv",
//                "text/csv",
//                resource.getInputStream());
//
//        postman.perform(multipart("/api/people/import").file(file))
//                .andDo(print())
//                .andExpect(status().isUnauthorized())
//                .andExpect(jsonPath("$.code").value(401))
//                .andExpect(jsonPath("$.status").value("Unauthorized"))
//                .andExpect(jsonPath("$.message").value("Full authentication is required to access this resource"))
//                .andExpect(jsonPath("$.uri").value("/api/people/import"))
//                .andExpect(jsonPath("$.method").value("POST"));
//    }
//
//    @Test
//    void shouldNotProcessFileUploadSuccessfullyWithInvalidToken() throws Exception {
//        ClassPathResource resource = new ClassPathResource("changesets/data/correct_people.csv");
//        MockMultipartFile file = new MockMultipartFile(
//                "file",
//                "correct_people.csv",
//                "text/csv",
//                resource.getInputStream());
//
//        postman.perform(multipart("/api/people/import").file(file)
//                        .header("Authorization", INVALID_TOKEN))
//                .andDo(print())
//                .andExpect(status().isUnauthorized())
//                .andExpect(jsonPath("$.code").value(401))
//                .andExpect(jsonPath("$.status").value("Unauthorized"))
//                .andExpect(jsonPath("$.message").value("Full authentication is required to access this resource"))
//                .andExpect(jsonPath("$.uri").value("/api/people/import"))
//                .andExpect(jsonPath("$.method").value("POST"));
//    }
//
//    @Test
//    void shouldNotProcessFileUploadWhenPeselAlreadyExistsWithRoleImporter() throws Exception {
//        ClassPathResource resource = new ClassPathResource("changesets/data/duplicate_pesel.csv");
//        MockMultipartFile file = new MockMultipartFile(
//                "file",
//                "duplicate_pesel.csv",
//                "text/csv",
//                resource.getInputStream());
//
//        MvcResult result = postman.perform(multipart("/api/people/imports").file(file)
//                        .header("Authorization", VALID_IMPORTER_TOKEN))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("The import has been accepted"))
//                .andExpect(jsonPath("$.taskId", notNullValue()))
//                .andReturn();
//
//        String jsonResponse = result.getResponse().getContentAsString();
//        String taskId = JsonPath.read(jsonResponse, "$.taskId");
//
//        Thread.sleep(1500);
//
//        postman.perform(get("/api/import/" + taskId + "/status")
//                        .header("Authorization", VALID_IMPORTER_TOKEN))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", notNullValue()))
//                .andExpect(jsonPath("$.taskId").value(taskId))
//                .andExpect(jsonPath("$.status").value("REJECTED"))
//                .andExpect(jsonPath("$.startTime").value(notNullValue()))
//                .andExpect(jsonPath("$.endTime").value(notNullValue()))
//                .andExpect(jsonPath("$.processedRows").value(0));
//    }
//
//    @Test
//    void shouldNotProcessFileUploadWhenEmailAlreadyExistsWithRoleImporter() throws Exception {
//        ClassPathResource resource = new ClassPathResource("changesets/data/duplicate_email.csv");
//        MockMultipartFile file = new MockMultipartFile(
//                "file",
//                "duplicate_email.csv",
//                "text/csv",
//                resource.getInputStream());
//
//        MvcResult result = postman.perform(multipart("/api/people/imports").file(file)
//                        .header("Authorization", VALID_IMPORTER_TOKEN))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("The import has been accepted"))
//                .andExpect(jsonPath("$.taskId", notNullValue()))
//                .andReturn();
//
//        String jsonResponse = result.getResponse().getContentAsString();
//        String taskId = JsonPath.read(jsonResponse, "$.taskId");
//
//        Thread.sleep(1500);
//
//        postman.perform(get("/api/import/" + taskId + "/status")
//                        .header("Authorization", VALID_IMPORTER_TOKEN))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", notNullValue()))
//                .andExpect(jsonPath("$.taskId").value(taskId))
//                .andExpect(jsonPath("$.status").value("REJECTED"))
//                .andExpect(jsonPath("$.startTime").value(notNullValue()))
//                .andExpect(jsonPath("$.endTime").value(notNullValue()))
//                .andExpect(jsonPath("$.processedRows").value(0));
//    }
//
//    @Test
//    void shouldNotProcessFileUploadWhenThePersonTypeIsNotSupportedWithRoleImporter() throws Exception {
//        ClassPathResource resource = new ClassPathResource("changesets/data/unsupported_person_type.csv");
//        MockMultipartFile file = new MockMultipartFile(
//                "file",
//                "unsupported_person_type.csv",
//                "text/csv",
//                resource.getInputStream());
//
//        MvcResult result = postman.perform(multipart("/api/people/imports").file(file)
//                        .header("Authorization", VALID_IMPORTER_TOKEN))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("The import has been accepted"))
//                .andExpect(jsonPath("$.taskId", notNullValue()))
//                .andReturn();
//
//        String jsonResponse = result.getResponse().getContentAsString();
//        String taskId = JsonPath.read(jsonResponse, "$.taskId");
//
//        Thread.sleep(1500);
//
//        postman.perform(get("/api/import/" + taskId + "/status")
//                        .header("Authorization", VALID_IMPORTER_TOKEN))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", notNullValue()))
//                .andExpect(jsonPath("$.taskId").value(taskId))
//                .andExpect(jsonPath("$.status").value("REJECTED"))
//                .andExpect(jsonPath("$.startTime").value(notNullValue()))
//                .andExpect(jsonPath("$.endTime").value(notNullValue()))
//                .andExpect(jsonPath("$.processedRows").value(0));
//    }
//
//    @Test
//    void shouldNotProcessFileUploadWhenFileFormatIsIncorrectRoleImporter() throws Exception {
//        ClassPathResource resource = new ClassPathResource("changesets/data/binary_file.bin");
//        MockMultipartFile file = new MockMultipartFile(
//                "file",
//                "binary_file.bin",
//                "application/octet-stream",
//                resource.getInputStream());
//
//        postman.perform(multipart("/api/people/imports").file(file)
//                        .header("Authorization", VALID_IMPORTER_TOKEN))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.code").value(400))
//                .andExpect(jsonPath("$.message").value("Unsupported file type: application/octet-stream"));
//    }

}

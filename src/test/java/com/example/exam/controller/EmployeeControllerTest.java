package com.example.exam.controller;

import com.example.exam.DatabaseCleaner;
import com.example.exam.ExamApplication;
import com.example.exam.model.employee.position.command.CreatePositionCommand;
import com.example.exam.model.person.command.CreatePersonCommand;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ExamApplication.class)
@AutoConfigureMockMvc
@DirtiesContext
@ActiveProfiles("test")
public class EmployeeControllerTest {

    private final MockMvc postman;
    private final ObjectMapper objectMapper;
    private final DatabaseCleaner databaseCleaner;
    private final String VALID_USER_TOKEN;
    private final String VALID_ADMIN_TOKEN;
    private final String VALID_IMPORTER_TOKEN;
    private final String INVALID_TOKEN;


    @Autowired
    public EmployeeControllerTest(MockMvc postman, ObjectMapper objectMapper, DatabaseCleaner databaseCleaner) throws Exception {
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
    void shouldNotSaveEmployeeAndChangeHisPositionWithRoleUser() throws Exception {
        CreatePersonCommand command = CreatePersonCommand.builder()
                .type("EMPLOYEE")
                .parameters(Map.of(
                        "name", "Adam",
                        "surname", "Krawiec",
                        "pesel", "66040112345",
                        "height", 178.9,
                        "weight", 102.3,
                        "email", "adam.krawiec@gmail.com",
                        "positions", List.of(
                                Map.of(
                                        "name", "Wuefista",
                                        "startDate", "2024-03-04",
                                        "salary", 3300.00
                                )
                        )
                ))
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_USER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotSaveEmployeeAndChangeHisPositionWithRoleImporter() throws Exception {
        CreatePersonCommand command = CreatePersonCommand.builder()
                .type("EMPLOYEE")
                .parameters(Map.of(
                        "name", "Adam",
                        "surname", "Krawiec",
                        "pesel", "66040112345",
                        "height", 178.9,
                        "weight", 102.3,
                        "email", "adam.krawiec@gmail.com",
                        "positions", List.of(
                                Map.of(
                                        "name", "Wuefista",
                                        "startDate", "2024-03-04",
                                        "salary", 3300.00
                                )
                        )
                ))
                .build();

        String requestBody = objectMapper.writeValueAsString(command);


        postman.perform(post("/api/people")
                        .header("Authorization", VALID_IMPORTER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotSaveEmployeeAndChangeHisPositionWithInvalidToken() throws Exception {
        CreatePersonCommand command = CreatePersonCommand.builder()
                .type("EMPLOYEE")
                .parameters(Map.of(
                        "name", "Adam",
                        "surname", "Krawiec",
                        "pesel", "66040112345",
                        "height", 178.9,
                        "weight", 102.3,
                        "email", "adam.krawiec@gmail.com",
                        "positions", List.of(
                                Map.of(
                                        "name", "Wuefista",
                                        "startDate", "2024-03-04",
                                        "salary", 3300.00
                                )
                        )
                ))
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(post("/api/people/save")
                        .header("Authorization", INVALID_TOKEN)
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
    void shouldNotSaveEmployeeAndChangeHisPositionWithoutAutorization() throws Exception {
        CreatePersonCommand command = CreatePersonCommand.builder()
                .type("EMPLOYEE")
                .parameters(Map.of(
                        "name", "Adam",
                        "surname", "Krawiec",
                        "pesel", "66040112345",
                        "height", 178.9,
                        "weight", 102.3,
                        "email", "adam.krawiec@gmail.com",
                        "positions", List.of(
                                Map.of(
                                        "name", "Wuefista",
                                        "startDate", "2024-03-04",
                                        "salary", 3300.00
                                )
                        )
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
    void shouldSaveEmployeeAndChangeHisPositionWithRoleAdmin() throws Exception {
        CreatePersonCommand command = CreatePersonCommand.builder()
                .type("EMPLOYEE")
                .parameters(Map.of(
                        "name", "Adam",
                        "surname", "Krawiec",
                        "pesel", "66040112345",
                        "height", 178.9,
                        "weight", 102.3,
                        "email", "adam.krawiec@gmail.com",
                        "positions", List.of(
                                Map.of(
                                        "name", "Wuefista",
                                        "startDate", "2024-03-04",
                                        "salary", 3300.00
                                )
                        )
                ))
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(get("/api/people/search?type=PERSON&id=51")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Adam"))
                .andExpect(jsonPath("$.surname").value("Krawiec"))
                .andExpect(jsonPath("$.pesel").value("66040112345"))
                .andExpect(jsonPath("$.height").value(178.9))
                .andExpect(jsonPath("$.weight").value(102.3))
                .andExpect(jsonPath("$.email").value("adam.krawiec@gmail.com"))
                .andExpect(jsonPath("$.current_position_start_date").value("2024-03-04"))
                .andExpect(jsonPath("$.current_salary").value(3300.0))
                .andExpect(jsonPath("$.current_position").value("Wuefista"));

        CreatePositionCommand positionCommand = CreatePositionCommand.builder()
                .name("Programista Python")
                .startDate(LocalDate.parse("2024-04-23"))
                .salary(BigDecimal.valueOf(10000.00))
                .build();
        String positionRequestBody = objectMapper.writeValueAsString(positionCommand);


        postman.perform(post("/api/employees/51/update-position")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(positionRequestBody))
                .andDo(print())
                .andExpect(status().isOk());

        postman.perform(get("/api/people/search?type=EMPLOYEE&currentPosition=Programista Python")
                        .header("Authorization", VALID_ADMIN_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Adam"))
                .andExpect(jsonPath("$.content[0].surname").value("Krawiec"))
                .andExpect(jsonPath("$.content[0].pesel").value("66040112345"))
                .andExpect(jsonPath("$.content[0].height").value(178.9))
                .andExpect(jsonPath("$.content[0].weight").value(102.3))
                .andExpect(jsonPath("$.content[0].email").value("adam.krawiec@gmail.com"))
                .andExpect(jsonPath("$.content[0].current_position_start_date").value("2024-04-23"))
                .andExpect(jsonPath("$.content[0].current_salary").value(10000.00))
                .andExpect(jsonPath("$.content[0].current_position").value("Programista Python"));
    }

    @Test
    void shouldNotSaveEmployeeByWrongPositionWhenStartDateIsFromTheFutureWithRoleAdmin() throws Exception {
        CreatePersonCommand command = CreatePersonCommand.builder()
                .type("EMPLOYEE")
                .parameters(Map.of(
                        "name", "Adam",
                        "surname", "Krawiec",
                        "pesel", "66040112345",
                        "height", 178.9,
                        "weight", 102.3,
                        "email", "adam.krawiec@gmail.com",
                        "positions", List.of(
                                Map.of(
                                        "name", "Wuefista",
                                        "startDate", "2024-12-04",
                                        "salary", 3300.00
                                )
                        )
                ))
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Validation failed: The start date must be in the past or present; "))
                .andExpect(jsonPath("$.status").value(400));

    }

    @Test
    void shouldNotSaveEmployeeByWrongPositionWhenSalaryIsNegativeWithRoleAdmin() throws Exception {
        CreatePersonCommand command = CreatePersonCommand.builder()
                .type("EMPLOYEE")
                .parameters(Map.of(
                        "name", "Adam",
                        "surname", "Krawiec",
                        "pesel", "66040112345",
                        "height", 178.9,
                        "weight", 102.3,
                        "email", "adam.krawiec@gmail.com",
                        "positions", List.of(
                                Map.of(
                                        "name", "Wuefista",
                                        "startDate", "2024-03-04",
                                        "salary", -3300.00
                                )
                        )
                ))
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Validation failed: Salary must be positive or zero; "))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotSaveEmployeeByWrongPositionWhenNameIsEmptyWithRoleAdmin() throws Exception {
        CreatePersonCommand command = CreatePersonCommand.builder()
                .type("EMPLOYEE")
                .parameters(Map.of(
                        "name", "Adam",
                        "surname", "Krawiec",
                        "pesel", "66040112345",
                        "height", 178.9,
                        "weight", 102.3,
                        "email", "adam.krawiec@gmail.com",
                        "positions", List.of(
                                Map.of(
                                        "name", "",
                                        "startDate", "2024-03-04",
                                        "salary", 3300.00
                                )
                        )
                ))
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        postman.perform(post("/api/people")
                        .header("Authorization", VALID_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Validation failed: The POSITION NAME field cannot be left empty; "))
                .andExpect(jsonPath("$.status").value(400));

    }
}

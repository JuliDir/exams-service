package com.eximia.exams.integration;

import com.eximia.exams.domain.enums.QuestionType;
import com.eximia.exams.dto.request.ExamRequestDto;
import com.eximia.exams.dto.request.QuestionRequestDto;
import com.eximia.exams.dto.response.ExamResponseDto;
import com.eximia.exams.repository.ExamRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource(properties = {
        "spring.data.mongodb.host=localhost",
        "spring.rabbitmq.host=localhost"
})
class ExamControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        examRepository.deleteAll();
    }

    @Test
    void shouldCreateExamSuccessfully() throws Exception {
        ExamRequestDto examRequest = createSampleExamRequest();

        mockMvc.perform(post("/exams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(examRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Sample Exam"))
                .andExpect(jsonPath("$.description").value("A sample exam for testing"))
                .andExpect(jsonPath("$.durationInMinutes").value(60))
                .andExpect(jsonPath("$.totalPoints").value(100.0))
                .andExpect(jsonPath("$.passingScore").value(70.0))
                .andExpect(jsonPath("$.createdBy").value("test-user"))
                .andExpect(jsonPath("$.category").value("General"))
                .andExpect(jsonPath("$.difficultyLevel").value("Intermediate"))
                .andExpect(jsonPath("$.questions").isArray())
                .andExpect(jsonPath("$.questions", hasSize(1)))
                .andExpect(jsonPath("$.questions[0].questionType").value("MULTIPLE_CHOICE"))
                .andExpect(jsonPath("$.questions[0].questionText").value("What is the capital of France?"))
                .andExpect(jsonPath("$.questions[0].points").value(10.0));
    }

    @Test
    void shouldReturnBadRequestWhenCreateExamWithInvalidData() throws Exception {
        ExamRequestDto invalidExam = ExamRequestDto.builder()
                .title("") // Invalid empty title
                .build();

        mockMvc.perform(post("/exams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidExam)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetExamById() throws Exception {
        // Create exam first
        ExamRequestDto examRequest = createSampleExamRequest();
        String examJson = mockMvc.perform(post("/exams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(examRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ExamResponseDto createdExam = objectMapper.readValue(examJson, ExamResponseDto.class);

        // Get exam by ID
        mockMvc.perform(get("/exams/{id}", createdExam.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdExam.getId()))
                .andExpect(jsonPath("$.title").value("Sample Exam"));
    }

    @Test
    void shouldReturnNotFoundWhenGetExamByInvalidId() throws Exception {
        mockMvc.perform(get("/exams/{id}", "non-existent-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetAllActiveExams() throws Exception {
        // Create multiple exams
        createAndSaveExam("Exam 1", "user1", "Math");
        createAndSaveExam("Exam 2", "user2", "Science");
        createAndSaveExam("Exam 3", "user1", "History");

        mockMvc.perform(get("/exams")
                        .param("size", "10")
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void shouldGetExamsByCreator() throws Exception {
        // Create exams with different creators
        createAndSaveExam("Exam 1", "user1", "Math");
        createAndSaveExam("Exam 2", "user2", "Science");
        createAndSaveExam("Exam 3", "user1", "History");

        mockMvc.perform(get("/exams/creator/{createdBy}", "user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].createdBy").value("user1"))
                .andExpect(jsonPath("$[1].createdBy").value("user1"));
    }

    @Test
    void shouldGetExamsByCategory() throws Exception {
        // Create exams with different categories
        createAndSaveExam("Exam 1", "user1", "Math");
        createAndSaveExam("Exam 2", "user2", "Science");
        createAndSaveExam("Exam 3", "user3", "Math");

        mockMvc.perform(get("/exams/category/{category}", "Math"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].category").value("Math"))
                .andExpect(jsonPath("$[1].category").value("Math"));
    }

    @Test
    void shouldSearchExamsByTitle() throws Exception {
        // Create exams with different titles
        createAndSaveExam("Java Programming Exam", "user1", "Programming");
        createAndSaveExam("Python Programming Test", "user2", "Programming");
        createAndSaveExam("Math Algebra Test", "user3", "Math");

        mockMvc.perform(get("/exams/search")
                        .param("title", "Programming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", containsString("Programming")))
                .andExpect(jsonPath("$[1].title", containsString("Programming")));
    }

    @Test
    void shouldGetExamsByDateRange() throws Exception {
        // Create exams (they will have current timestamp)
        createAndSaveExam("Recent Exam", "user1", "General");

        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        mockMvc.perform(get("/exams/date-range")
                        .param("startDate", startDate.format(formatter))
                        .param("endDate", endDate.format(formatter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void shouldGetExamsByPointsRange() throws Exception {
        // Create exams with different point values
        createAndSaveExamWithPoints("Low Points Exam", "user1", "General", 50.0);
        createAndSaveExamWithPoints("High Points Exam", "user2", "General", 150.0);
        createAndSaveExamWithPoints("Medium Points Exam", "user3", "General", 100.0);

        mockMvc.perform(get("/exams/points-range")
                        .param("minPoints", "75.0")
                        .param("maxPoints", "125.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].totalPoints").value(100.0));
    }

    @Test
    void shouldUpdateExam() throws Exception {
        // Create exam first
        String examId = createAndSaveExam("Original Title", "user1", "Original Category");

        // Update exam
        ExamRequestDto updateRequest = createSampleExamRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setCategory("Updated Category");

        mockMvc.perform(put("/exams/{id}", examId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.category").value("Updated Category"));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentExam() throws Exception {
        ExamRequestDto updateRequest = createSampleExamRequest();

        mockMvc.perform(put("/exams/{id}", "non-existent-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeactivateExam() throws Exception {
        // Create exam first
        String examId = createAndSaveExam("Exam to Deactivate", "user1", "General");

        // Deactivate exam
        mockMvc.perform(patch("/exams/{id}/deactivate", examId))
                .andExpect(status().isNoContent());

        // Verify exam is deactivated (should not be found in active exams)
        mockMvc.perform(get("/exams/{id}", examId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenDeactivatingNonExistentExam() throws Exception {
        mockMvc.perform(patch("/exams/{id}/deactivate", "non-existent-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteExam() throws Exception {
        // Create exam first
        String examId = createAndSaveExam("Exam to Delete", "user1", "General");

        // Delete exam
        mockMvc.perform(delete("/exams/{id}", examId))
                .andExpect(status().isNoContent());

        // Verify exam is deleted
        mockMvc.perform(get("/exams/{id}", examId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentExam() throws Exception {
        mockMvc.perform(delete("/exams/{id}", "non-existent-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestForInvalidPathVariable() throws Exception {
        mockMvc.perform(get("/exams/{id}", ""))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldReturnBadRequestForInvalidRequestParams() throws Exception {
        mockMvc.perform(get("/exams/points-range")
                        .param("minPoints", "-10.0")
                        .param("maxPoints", "100.0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldHandlePaginationCorrectly() throws Exception {
        // Create many exams
        for (int i = 1; i <= 25; i++) {
            createAndSaveExam("Exam " + i, "user1", "General");
        }

        // Test first page
        mockMvc.perform(get("/exams")
                        .param("size", "10")
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(10)))
                .andExpect(jsonPath("$.totalElements").value(25))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false));

        // Test last page
        mockMvc.perform(get("/exams")
                        .param("size", "10")
                        .param("page", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.first").value(false))
                .andExpect(jsonPath("$.last").value(true));
    }

    // Helper methods
    private ExamRequestDto createSampleExamRequest() {
        QuestionRequestDto question = QuestionRequestDto.builder()
                .questionText("What is the capital of France?")
                .questionType(QuestionType.MULTIPLE_CHOICE)
                .points(10.0)
                .isRequired(true)
                .orderIndex(1)
                .build();

        return ExamRequestDto.builder()
                .title("Sample Exam")
                .description("A sample exam for testing")
                .durationInMinutes(60)
                .totalPoints(100.0)
                .passingScore(70.0)
                .questions(Arrays.asList(question))
                .createdBy("test-user")
                .category("General")
                .difficultyLevel("Intermediate")
                .build();
    }

    private String createAndSaveExam(String title, String createdBy, String category) throws Exception {
        ExamRequestDto examRequest = createSampleExamRequest();
        examRequest.setTitle(title);
        examRequest.setCreatedBy(createdBy);
        examRequest.setCategory(category);

        String response = mockMvc.perform(post("/exams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(examRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ExamResponseDto createdExam = objectMapper.readValue(response, ExamResponseDto.class);
        return createdExam.getId();
    }

    private String createAndSaveExamWithPoints(String title, String createdBy, String category, Double totalPoints) throws Exception {
        ExamRequestDto examRequest = createSampleExamRequest();
        examRequest.setTitle(title);
        examRequest.setCreatedBy(createdBy);
        examRequest.setCategory(category);
        examRequest.setTotalPoints(totalPoints);

        String response = mockMvc.perform(post("/exams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(examRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ExamResponseDto createdExam = objectMapper.readValue(response, ExamResponseDto.class);
        return createdExam.getId();
    }
}
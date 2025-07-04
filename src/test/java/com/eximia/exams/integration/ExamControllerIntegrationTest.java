package com.eximia.exams.integration;

import com.eximia.exams.domain.entities.Exam;
import com.eximia.exams.domain.enums.QuestionType;
import com.eximia.exams.dto.request.ExamRequestDto;
import com.eximia.exams.dto.request.OptionRequestDto;
import com.eximia.exams.dto.request.QuestionRequestDto;
import com.eximia.exams.dto.response.ExamResponseDto;
import com.eximia.exams.repository.ExamRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ExamControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        examRepository.deleteAll();
    }

    @Test
    void shouldCreateExamSuccessfully() throws Exception {
        ExamRequestDto examRequest = createValidExamRequest();

        mockMvc.perform(post("/exams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(examRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(examRequest.getTitle()))
                .andExpect(jsonPath("$.description").value(examRequest.getDescription()))
                .andExpect(jsonPath("$.durationInMinutes").value(examRequest.getDurationInMinutes()))
                .andExpect(jsonPath("$.passingScore").value(examRequest.getPassingScore()))
                .andExpect(jsonPath("$.subject").value(examRequest.getSubject()))
                .andExpect(jsonPath("$.difficultyLevel").value(examRequest.getDifficultyLevel()))
                .andExpect(jsonPath("$.questions").isArray())
                .andExpect(jsonPath("$.questions", hasSize(2)))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void shouldReturnBadRequestWhenCreateExamWithEmptyTitle() throws Exception {
        ExamRequestDto examRequest = createValidExamRequest();
        examRequest.setTitle("");

        mockMvc.perform(post("/exams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(examRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.title").exists());
    }

    @Test
    void shouldReturnBadRequestWhenCreateExamWithoutQuestions() throws Exception {
        ExamRequestDto examRequest = createValidExamRequest();
        examRequest.setQuestions(List.of());

        mockMvc.perform(post("/exams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(examRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.questions").exists());
    }

    @Test
    void shouldGetExamById() throws Exception {
        // Create exam first
        ExamRequestDto examRequest = createValidExamRequest();
        String response = mockMvc.perform(post("/exams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(examRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ExamResponseDto createdExam = objectMapper.readValue(response, ExamResponseDto.class);

        // Get exam by ID
        mockMvc.perform(get("/exams/{id}", createdExam.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdExam.getId()))
                .andExpect(jsonPath("$.title").value(examRequest.getTitle()))
                .andExpect(jsonPath("$.questions", hasSize(2)));
    }

    @Test
    void shouldReturnNotFoundWhenGetExamByInvalidId() throws Exception {
        mockMvc.perform(get("/exams/{id}", "507f1f77bcf86cd799439011"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Exam not found with ID: 507f1f77bcf86cd799439011"));
    }

    @Test
    void shouldGetExamsByCriteria() throws Exception {
        // Create multiple exams
        createExam("Java Programming", "Programming", "Advanced", "john.doe");
        createExam("Python Basics", "Programming", "Beginner", "jane.smith");
        createExam("Math Algebra", "Mathematics", "Intermediate", "john.doe");

        // Test search by subject
        mockMvc.perform(get("/exams")
                        .param("subject", "Programming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].subject").value("Programming"))
                .andExpect(jsonPath("$.content[1].subject").value("Programming"));

        // Test search by difficulty level
        mockMvc.perform(get("/exams")
                        .param("difficultyLevel", "Advanced"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].difficultyLevel").value("Advanced"));

        // Test search by creator
        mockMvc.perform(get("/exams")
                        .param("createdBy", "john.doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    void shouldSearchExamsByText() throws Exception {
        // Create exams
        createExam("Java Advanced Programming", "Programming", "Advanced", "user1");
        createExam("Basic Python Course", "Programming", "Beginner", "user2");
        createExam("Calculus Exam", "Mathematics", "Advanced", "user3");

        mockMvc.perform(get("/exams/search")
                        .param("searchText", "Programming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldCountExamsByCriteria() throws Exception {
        // Create exams
        createExam("Exam 1", "Programming", "Advanced", "user1");
        createExam("Exam 2", "Programming", "Beginner", "user1");
        createExam("Exam 3", "Mathematics", "Advanced", "user2");

        mockMvc.perform(get("/exams/count")
                        .param("subject", "Programming"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @Test
    void shouldUpdateExam() throws Exception {
        // Create exam
        ExamRequestDto originalExam = createValidExamRequest();
        String response = mockMvc.perform(post("/exams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(originalExam)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ExamResponseDto createdExam = objectMapper.readValue(response, ExamResponseDto.class);

        // Update exam
        ExamRequestDto updateRequest = createValidExamRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setDescription("Updated Description");
        updateRequest.setDurationInMinutes(120);

        mockMvc.perform(put("/exams/{id}", createdExam.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.durationInMinutes").value(120))
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void shouldDeleteExam() throws Exception {
        // Create exam
        String examId = createExam("Exam to Delete", "Programming", "Beginner", "user1");

        // Delete exam
        mockMvc.perform(delete("/exams/{id}", examId))
                .andExpect(status().isNoContent());

        // Verify exam is deleted
        mockMvc.perform(get("/exams/{id}", examId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldHandlePagination() throws Exception {
        // Create 25 exams
        for (int i = 1; i <= 25; i++) {
            createExam("Exam " + i, "Subject", "Level", "user");
        }

        // Test first page
        mockMvc.perform(get("/exams")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(10)))
                .andExpect(jsonPath("$.totalElements").value(25))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.number").value(0));

        // Test last page
        mockMvc.perform(get("/exams")
                        .param("page", "2")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.number").value(2))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    void shouldFilterByDateRange() throws Exception {
        // Create exams
        createExam("Recent Exam", "Subject", "Level", "user");

        // Wait a bit to ensure different timestamps
        Thread.sleep(100);

        mockMvc.perform(get("/exams")
                        .param("createdAtFrom", "2020-01-01T00:00:00")
                        .param("createdAtTo", "2030-12-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(greaterThan(0)));
    }

    @Test
    void shouldFilterByPointsRange() throws Exception {
        // Create exams with specific question structure
        createExamWithTotalPoints("Low Points", 50.0);
        createExamWithTotalPoints("Medium Points", 100.0);
        createExamWithTotalPoints("High Points", 150.0);

        mockMvc.perform(get("/exams")
                        .param("totalPointsMin", "75")
                        .param("totalPointsMax", "125"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title").value("Medium Points"));
    }

    // Helper methods
    private ExamRequestDto createValidExamRequest() {
        List<OptionRequestDto> options1 = Arrays.asList(
                OptionRequestDto.builder()
                        .optionText("Paris")
                        .isCorrect(true)
                        .points(10.0)
                        .orderIndex(1)
                        .build(),
                OptionRequestDto.builder()
                        .optionText("London")
                        .isCorrect(false)
                        .points(0.0)
                        .orderIndex(2)
                        .build(),
                OptionRequestDto.builder()
                        .optionText("Berlin")
                        .isCorrect(false)
                        .points(0.0)
                        .orderIndex(3)
                        .build(),
                OptionRequestDto.builder()
                        .optionText("Madrid")
                        .isCorrect(false)
                        .points(0.0)
                        .orderIndex(4)
                        .build()
        );

        List<OptionRequestDto> options2 = Arrays.asList(
                OptionRequestDto.builder()
                        .optionText("True")
                        .isCorrect(true)
                        .points(5.0)
                        .orderIndex(1)
                        .build(),
                OptionRequestDto.builder()
                        .optionText("False")
                        .isCorrect(false)
                        .points(0.0)
                        .orderIndex(2)
                        .build()
        );

        List<QuestionRequestDto> questions = Arrays.asList(
                QuestionRequestDto.builder()
                        .questionText("What is the capital of France?")
                        .questionType(QuestionType.MULTIPLE_CHOICE)
                        .points(10.0)
                        .orderIndex(1)
                        .options(options1)
                        .explanation("Paris is the capital city of France")
                        .build(),
                QuestionRequestDto.builder()
                        .questionText("Is Java an object-oriented language?")
                        .questionType(QuestionType.TRUE_FALSE)
                        .points(5.0)
                        .orderIndex(2)
                        .options(options2)
                        .explanation("Java is indeed an object-oriented programming language")
                        .build()
        );

        return ExamRequestDto.builder()
                .title("Sample Exam")
                .description("A comprehensive exam for testing")
                .durationInMinutes(90)
                .passingScore(70.0)
                .questions(questions)
                .subject("Programming")
                .difficultyLevel("Intermediate")
                .allowMultipleChoice(true)
                .allowTrueFalse(true)
                .build();
    }

    private String createExam(String title, String subject, String level, String createdBy) throws Exception {
        ExamRequestDto request = createValidExamRequest();
        request.setTitle(title);
        request.setSubject(subject);
        request.setDifficultyLevel(level);

        Exam exam = Exam.builder()
                .title(title)
                .subject(subject)
                .difficultyLevel(level)
                .createdBy(createdBy)
                .description("Test exam")
                .durationInMinutes(60)
                .passingScore(60.0)
                .totalPoints(100.0)
                .allowMultipleChoice(true)
                .allowTrueFalse(true)
                .build();

        Exam saved = examRepository.save(exam);
        return saved.getId();
    }

    private void createExamWithTotalPoints(String title, Double totalPoints) throws Exception {
        Exam exam = Exam.builder()
                .title(title)
                .subject("Test")
                .difficultyLevel("Test")
                .createdBy("test")
                .description("Test exam")
                .durationInMinutes(60)
                .passingScore(60.0)
                .totalPoints(totalPoints)
                .allowMultipleChoice(true)
                .allowTrueFalse(true)
                .build();

        examRepository.save(exam);
    }
}
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

import java.util.Arrays;

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

        mockMvc.perform(post("/api/v1/exams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(examRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Sample Exam"))
                .andExpect(jsonPath("$.questions").isArray())
                .andExpect(jsonPath("$.questions[0].questionType").value("MULTIPLE_CHOICE"));
    }

    @Test
    void shouldGetExamById() throws Exception {
        // Create exam first
        ExamRequestDto examRequest = createSampleExamRequest();
        String examJson = mockMvc.perform(post("/api/v1/exams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(examRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ExamResponseDto createdExam = objectMapper.readValue(examJson, ExamResponseDto.class);

        // Get exam by ID
        mockMvc.perform(get("/api/v1/exams/{id}", createdExam.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdExam.getId()))
                .andExpect(jsonPath("$.title").value("Sample Exam"));
    }

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
}

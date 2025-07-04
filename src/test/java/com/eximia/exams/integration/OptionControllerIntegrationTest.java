package com.eximia.exams.integration;

import com.eximia.exams.domain.entities.Exam;
import com.eximia.exams.domain.entities.Option;
import com.eximia.exams.domain.entities.Question;
import com.eximia.exams.domain.enums.QuestionType;
import com.eximia.exams.dto.request.OptionRequestDto;
import com.eximia.exams.repository.ExamRepository;
import com.eximia.exams.repository.OptionRepository;
import com.eximia.exams.repository.QuestionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OptionControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String questionId;
    private String examId;

    @BeforeEach
    void init() {
        optionRepository.deleteAll();
        questionRepository.deleteAll();
        examRepository.deleteAll();

        // Create test exam
        Exam exam = Exam.builder()
                .title("Test Exam")
                .description("Exam for testing options")
                .durationInMinutes(60)
                .passingScore(70.0)
                .subject("Testing")
                .difficultyLevel("Intermediate")
                .totalPoints(100.0)
                .build();
        examId = examRepository.save(exam).getId();

        // Create test question
        Question question = Question.builder()
                .questionText("Test Question")
                .questionType(QuestionType.MULTIPLE_CHOICE)
                .points(10.0)
                .examId(examId)
                .orderIndex(1)
                .build();
        questionId = questionRepository.save(question).getId();
    }

    @Test
    void shouldCreateOptionForQuestion() throws Exception {
        OptionRequestDto request = OptionRequestDto.builder()
                .optionText("This is a test option")
                .isCorrect(true)
                .points(10.0)
                .orderIndex(1)
                .explanation("This is the correct answer")
                .build();

        mockMvc.perform(post("/options/question/{questionId}", questionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.optionText").value(request.getOptionText()))
                .andExpect(jsonPath("$.isCorrect").value(true))
                .andExpect(jsonPath("$.points").value(10.0))
                .andExpect(jsonPath("$.orderIndex").value(1))
                .andExpect(jsonPath("$.explanation").value(request.getExplanation()))
                .andExpect(jsonPath("$.questionId").value(questionId))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void shouldReturnNotFoundWhenCreateOptionForNonExistentQuestion() throws Exception {
        OptionRequestDto request = createValidOptionRequest();

        mockMvc.perform(post("/options/question/{questionId}", "507f1f77bcf86cd799439011")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldValidateOptionRequest() throws Exception {
        OptionRequestDto request = OptionRequestDto.builder()
                .optionText("") // Invalid empty text
                .isCorrect(null) // Invalid null
                .points(-5.0) // Invalid negative points
                .orderIndex(0) // Invalid index < 1
                .build();

        mockMvc.perform(post("/options/question/{questionId}", questionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.optionText").exists())
                .andExpect(jsonPath("$.fieldErrors.isCorrect").exists())
                .andExpect(jsonPath("$.fieldErrors.points").exists())
                .andExpect(jsonPath("$.fieldErrors.orderIndex").exists());
    }

    @Test
    void shouldGetOptionById() throws Exception {
        String optionId = createOption("Test Option", true, 10.0, 1);

        mockMvc.perform(get("/options/{id}", optionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(optionId))
                .andExpect(jsonPath("$.optionText").value("Test Option"))
                .andExpect(jsonPath("$.isCorrect").value(true));
    }

    @Test
    void shouldGetOptionsByCriteria() throws Exception {
        // Create multiple options
        createOption("Correct Option 1", true, 5.0, 1);
        createOption("Incorrect Option", false, 0.0, 2);
        createOption("Correct Option 2", true, 5.0, 3);

        // Filter by correct answers
        mockMvc.perform(get("/options")
                        .param("isCorrect", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));

        // Filter by question ID
        mockMvc.perform(get("/options")
                        .param("questionId", questionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)));
    }

    @Test
    void shouldSearchOptionsByText() throws Exception {
        createOption("Java is a programming language", true, 10.0, 1);
        createOption("Python is also a programming language", false, 0.0, 2);
        createOption("HTML is a markup language", false, 0.0, 3);

        mockMvc.perform(get("/options/search")
                        .param("searchText", "programming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldCountOptions() throws Exception {
        createOption("Option 1", true, 10.0, 1);
        createOption("Option 2", false, 0.0, 2);
        createOption("Option 3", false, 0.0, 3);

        mockMvc.perform(get("/options/count")
                        .param("questionId", questionId))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));

        mockMvc.perform(get("/options/count")
                        .param("isCorrect", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    void shouldUpdateOption() throws Exception {
        String optionId = createOption("Original Text", false, 0.0, 1);

        OptionRequestDto updateRequest = OptionRequestDto.builder()
                .optionText("Updated Text")
                .isCorrect(true)
                .points(10.0)
                .orderIndex(2)
                .explanation("Now this is correct")
                .build();

        mockMvc.perform(put("/options/{id}", optionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.optionText").value("Updated Text"))
                .andExpect(jsonPath("$.isCorrect").value(true))
                .andExpect(jsonPath("$.points").value(10.0))
                .andExpect(jsonPath("$.explanation").value("Now this is correct"));
    }

    @Test
    void shouldDeleteOption() throws Exception {
        String optionId = createOption("Option to Delete", false, 0.0, 1);

        mockMvc.perform(delete("/options/{id}", optionId))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/options/{id}", optionId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteAllOptionsForQuestion() throws Exception {
        // Create multiple options
        createOption("Option 1", true, 10.0, 1);
        createOption("Option 2", false, 0.0, 2);
        createOption("Option 3", false, 0.0, 3);

        mockMvc.perform(delete("/options/question/{questionId}", questionId))
                .andExpect(status().isNoContent());

        // Verify all options are deleted
        mockMvc.perform(get("/options")
                        .param("questionId", questionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    void shouldFilterByPointsRange() throws Exception {
        createOption("Low Points", false, 0.0, 1);
        createOption("Medium Points", true, 5.0, 2);
        createOption("High Points", true, 10.0, 3);

        mockMvc.perform(get("/options")
                        .param("pointsMin", "3")
                        .param("pointsMax", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].points").value(5.0));
    }

    @Test
    void shouldFilterByOrderIndexRange() throws Exception {
        createOption("First", true, 10.0, 1);
        createOption("Second", false, 0.0, 2);
        createOption("Third", false, 0.0, 3);
        createOption("Fourth", false, 0.0, 4);

        mockMvc.perform(get("/options")
                        .param("orderIndexMin", "2")
                        .param("orderIndexMax", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    void shouldHandlePaginationForOptions() throws Exception {
        // Create 15 options
        for (int i = 1; i <= 15; i++) {
            createOption("Option " + i, i == 1, i == 1 ? 10.0 : 0.0, i);
        }

        mockMvc.perform(get("/options")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.totalElements").value(15))
                .andExpect(jsonPath("$.totalPages").value(3));
    }

    @Test
    void shouldValidateExplanationLength() throws Exception {
        String longExplanation = "a".repeat(501); // Exceeds 500 character limit

        OptionRequestDto request = OptionRequestDto.builder()
                .optionText("Valid option text")
                .isCorrect(true)
                .points(10.0)
                .orderIndex(1)
                .explanation(longExplanation)
                .build();

        mockMvc.perform(post("/options/question/{questionId}", questionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.explanation").exists());
    }

    @Test
    void shouldCreateOptionsWithDifferentOrderIndices() throws Exception {
        // Create options with specific order
        for (int i = 1; i <= 4; i++) {
            OptionRequestDto request = OptionRequestDto.builder()
                    .optionText("Option " + i)
                    .isCorrect(i == 1)
                    .points(i == 1 ? 10.0 : 0.0)
                    .orderIndex(i)
                    .build();

            mockMvc.perform(post("/options/question/{questionId}", questionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.orderIndex").value(i));
        }

        // Verify order
        mockMvc.perform(get("/options")
                        .param("questionId", questionId)
                        .param("sort", "orderIndex,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(4)))
                .andExpect(jsonPath("$.content[0].orderIndex").value(1))
                .andExpect(jsonPath("$.content[3].orderIndex").value(4));
    }

    // Helper methods
    private OptionRequestDto createValidOptionRequest() {
        return OptionRequestDto.builder()
                .optionText("Valid Option")
                .isCorrect(false)
                .points(0.0)
                .orderIndex(1)
                .explanation("This is an explanation")
                .build();
    }

    private String createOption(String text, boolean isCorrect, Double points, Integer orderIndex) {
        Option option = Option.builder()
                .optionText(text)
                .isCorrect(isCorrect)
                .points(points)
                .orderIndex(orderIndex)
                .questionId(questionId)
                .build();

        return optionRepository.save(option).getId();
    }
}
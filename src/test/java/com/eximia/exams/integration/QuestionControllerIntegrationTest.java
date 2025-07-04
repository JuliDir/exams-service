package com.eximia.exams.integration;

import com.eximia.exams.domain.entities.Exam;
import com.eximia.exams.domain.entities.Question;
import com.eximia.exams.domain.enums.QuestionType;
import com.eximia.exams.dto.request.OptionRequestDto;
import com.eximia.exams.dto.request.QuestionRequestDto;
import com.eximia.exams.repository.ExamRepository;
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

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class QuestionControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String examId;

    @BeforeEach
    void init() {
        questionRepository.deleteAll();
        examRepository.deleteAll();

        // Create a test exam
        Exam exam = Exam.builder()
                .title("Test Exam")
                .description("Exam for testing questions")
                .durationInMinutes(60)
                .passingScore(70.0)
                .subject("Testing")
                .difficultyLevel("Intermediate")
                .allowMultipleChoice(true)
                .allowTrueFalse(true)
                .totalPoints(100.0)
                .build();

        examId = examRepository.save(exam).getId();
    }

    @Test
    void shouldCreateQuestionForExam() throws Exception {
        QuestionRequestDto request = createMultipleChoiceQuestion();

        mockMvc.perform(post("/questions/exam/{examId}", examId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.questionText").value(request.getQuestionText()))
                .andExpect(jsonPath("$.questionType").value("MULTIPLE_CHOICE"))
                .andExpect(jsonPath("$.points").value(request.getPoints()))
                .andExpect(jsonPath("$.examId").value(examId))
                .andExpect(jsonPath("$.options", hasSize(4)))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void shouldReturnNotFoundWhenCreateQuestionForNonExistentExam() throws Exception {
        QuestionRequestDto request = createMultipleChoiceQuestion();

        mockMvc.perform(post("/questions/exam/{examId}", "507f1f77bcf86cd799439011")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldValidateMultipleChoiceQuestionHasExactlyOneCorrectAnswer() throws Exception {
        QuestionRequestDto request = createMultipleChoiceQuestion();
        // Set all options as correct (invalid)
        request.getOptions().forEach(opt -> opt.setIsCorrect(true));

        mockMvc.perform(post("/questions/exam/{examId}", examId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateTrueFalseQuestion() throws Exception {
        QuestionRequestDto request = createTrueFalseQuestion();

        mockMvc.perform(post("/questions/exam/{examId}", examId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.questionType").value("TRUE_FALSE"))
                .andExpect(jsonPath("$.options", hasSize(2)));
    }

    @Test
    void shouldCreateMultipleSelectionQuestion() throws Exception {
        QuestionRequestDto request = createMultipleSelectionQuestion();

        mockMvc.perform(post("/questions/exam/{examId}", examId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.questionType").value("MULTIPLE_SELECTION"))
                .andExpect(jsonPath("$.options", hasSize(5)));
    }

    @Test
    void shouldGetQuestionById() throws Exception {
        // Create question first
        String questionId = createQuestion("Test Question", QuestionType.MULTIPLE_CHOICE, 10.0);

        mockMvc.perform(get("/questions/{id}", questionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(questionId))
                .andExpect(jsonPath("$.questionText").value("Test Question"));
    }

    @Test
    void shouldGetQuestionsByCriteria() throws Exception {
        // Create multiple questions
        createQuestion("Java Question", QuestionType.MULTIPLE_CHOICE, 10.0);
        createQuestion("Python Question", QuestionType.TRUE_FALSE, 5.0);
        createQuestion("JavaScript Question", QuestionType.MULTIPLE_CHOICE, 10.0);

        // Filter by question type
        mockMvc.perform(get("/questions")
                        .param("questionType", "MULTIPLE_CHOICE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));

        // Filter by exam ID
        mockMvc.perform(get("/questions")
                        .param("examId", examId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)));
    }

    @Test
    void shouldSearchQuestionsByText() throws Exception {
        createQuestion("What is polymorphism in OOP?", QuestionType.MULTIPLE_CHOICE, 10.0);
        createQuestion("Explain inheritance", QuestionType.MULTIPLE_CHOICE, 10.0);
        createQuestion("True or False: Java supports multiple inheritance", QuestionType.TRUE_FALSE, 5.0);

        mockMvc.perform(get("/questions/search")
                        .param("searchText", "inheritance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldCountQuestions() throws Exception {
        createQuestion("Q1", QuestionType.MULTIPLE_CHOICE, 10.0);
        createQuestion("Q2", QuestionType.MULTIPLE_CHOICE, 10.0);
        createQuestion("Q3", QuestionType.TRUE_FALSE, 5.0);

        mockMvc.perform(get("/questions/count")
                        .param("questionType", "MULTIPLE_CHOICE"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @Test
    void shouldUpdateQuestion() throws Exception {
        // Create question
        String questionId = createQuestion("Original Question", QuestionType.MULTIPLE_CHOICE, 10.0);

        // Update request
        QuestionRequestDto updateRequest = createMultipleChoiceQuestion();
        updateRequest.setQuestionText("Updated Question Text");
        updateRequest.setPoints(15.0);

        mockMvc.perform(put("/questions/{id}", questionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionText").value("Updated Question Text"))
                .andExpect(jsonPath("$.points").value(15.0));
    }

    @Test
    void shouldDeleteQuestion() throws Exception {
        String questionId = createQuestion("Question to Delete", QuestionType.MULTIPLE_CHOICE, 10.0);

        mockMvc.perform(delete("/questions/{id}", questionId))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/questions/{id}", questionId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteAllQuestionsForExam() throws Exception {
        // Create multiple questions
        createQuestion("Q1", QuestionType.MULTIPLE_CHOICE, 10.0);
        createQuestion("Q2", QuestionType.TRUE_FALSE, 5.0);
        createQuestion("Q3", QuestionType.MULTIPLE_CHOICE, 10.0);

        mockMvc.perform(delete("/questions/exam/{examId}", examId))
                .andExpect(status().isNoContent());

        // Verify all questions are deleted
        mockMvc.perform(get("/questions")
                        .param("examId", examId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    void shouldFilterByPointsRange() throws Exception {
        createQuestion("Low Points", QuestionType.MULTIPLE_CHOICE, 5.0);
        createQuestion("Medium Points", QuestionType.MULTIPLE_CHOICE, 10.0);
        createQuestion("High Points", QuestionType.MULTIPLE_CHOICE, 20.0);

        mockMvc.perform(get("/questions")
                        .param("pointsMin", "8")
                        .param("pointsMax", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].points").value(10.0));
    }

    @Test
    void shouldHandlePaginationForQuestions() throws Exception {
        // Create 15 questions
        for (int i = 1; i <= 15; i++) {
            createQuestion("Question " + i, QuestionType.MULTIPLE_CHOICE, 10.0);
        }

        mockMvc.perform(get("/questions")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.totalElements").value(15))
                .andExpect(jsonPath("$.totalPages").value(3));
    }

    @Test
    void shouldCreateDragAndDropQuestion() throws Exception {
        List<OptionRequestDto> options = Arrays.asList(
                OptionRequestDto.builder()
                        .optionText("First step")
                        .isCorrect(true)
                        .points(2.5)
                        .orderIndex(1)
                        .build(),
                OptionRequestDto.builder()
                        .optionText("Second step")
                        .isCorrect(true)
                        .points(2.5)
                        .orderIndex(2)
                        .build(),
                OptionRequestDto.builder()
                        .optionText("Third step")
                        .isCorrect(true)
                        .points(2.5)
                        .orderIndex(3)
                        .build(),
                OptionRequestDto.builder()
                        .optionText("Fourth step")
                        .isCorrect(true)
                        .points(2.5)
                        .orderIndex(4)
                        .build()
        );

        QuestionRequestDto request = QuestionRequestDto.builder()
                .questionText("Arrange the steps in correct order")
                .questionType(QuestionType.DRAG_AND_DROP)
                .points(10.0)
                .orderIndex(1)
                .options(options)
                .build();

        mockMvc.perform(post("/questions/exam/{examId}", examId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.questionType").value("DRAG_AND_DROP"))
                .andExpect(jsonPath("$.options", hasSize(4)));
    }

    // Helper methods
    private QuestionRequestDto createMultipleChoiceQuestion() {
        List<OptionRequestDto> options = Arrays.asList(
                OptionRequestDto.builder()
                        .optionText("Option A")
                        .isCorrect(true)
                        .points(10.0)
                        .orderIndex(1)
                        .build(),
                OptionRequestDto.builder()
                        .optionText("Option B")
                        .isCorrect(false)
                        .points(0.0)
                        .orderIndex(2)
                        .build(),
                OptionRequestDto.builder()
                        .optionText("Option C")
                        .isCorrect(false)
                        .points(0.0)
                        .orderIndex(3)
                        .build(),
                OptionRequestDto.builder()
                        .optionText("Option D")
                        .isCorrect(false)
                        .points(0.0)
                        .orderIndex(4)
                        .build()
        );

        return QuestionRequestDto.builder()
                .questionText("What is the correct answer?")
                .questionType(QuestionType.MULTIPLE_CHOICE)
                .points(10.0)
                .orderIndex(1)
                .options(options)
                .explanation("Option A is correct because...")
                .build();
    }

    private QuestionRequestDto createTrueFalseQuestion() {
        List<OptionRequestDto> options = Arrays.asList(
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

        return QuestionRequestDto.builder()
                .questionText("Java is a compiled language")
                .questionType(QuestionType.TRUE_FALSE)
                .points(5.0)
                .orderIndex(2)
                .options(options)
                .explanation("Java is compiled to bytecode")
                .build();
    }

    private QuestionRequestDto createMultipleSelectionQuestion() {
        List<OptionRequestDto> options = Arrays.asList(
                OptionRequestDto.builder()
                        .optionText("Encapsulation")
                        .isCorrect(true)
                        .points(2.5)
                        .orderIndex(1)
                        .build(),
                OptionRequestDto.builder()
                        .optionText("Inheritance")
                        .isCorrect(true)
                        .points(2.5)
                        .orderIndex(2)
                        .build(),
                OptionRequestDto.builder()
                        .optionText("Compilation")
                        .isCorrect(false)
                        .points(0.0)
                        .orderIndex(3)
                        .build(),
                OptionRequestDto.builder()
                        .optionText("Polymorphism")
                        .isCorrect(true)
                        .points(2.5)
                        .orderIndex(4)
                        .build(),
                OptionRequestDto.builder()
                        .optionText("Abstraction")
                        .isCorrect(true)
                        .points(2.5)
                        .orderIndex(5)
                        .build()
        );

        return QuestionRequestDto.builder()
                .questionText("Select all OOP principles")
                .questionType(QuestionType.MULTIPLE_SELECTION)
                .points(10.0)
                .orderIndex(3)
                .options(options)
                .explanation("The four main OOP principles are...")
                .build();
    }

    private String createQuestion(String text, QuestionType type, Double points) {
        Question question = Question.builder()
                .questionText(text)
                .questionType(type)
                .points(points)
                .examId(examId)
                .orderIndex(1)
                .build();

        return questionRepository.save(question).getId();
    }
}
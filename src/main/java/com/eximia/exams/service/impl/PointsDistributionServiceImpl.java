package com.eximia.exams.service.impl;

import com.eximia.exams.dto.request.ExamRequestDto;
import com.eximia.exams.dto.request.QuestionRequestDto;
import com.eximia.exams.dto.request.OptionRequestDto;
import com.eximia.exams.exception.CustomException;
import com.eximia.exams.service.PointsDistributionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service
public class PointsDistributionServiceImpl implements PointsDistributionService {

    private static final double MAX_EXAM_POINTS = 100.0;
    private static final int DECIMAL_SCALE = 2;

    public void distributeExamPoints(ExamRequestDto examRequestDto) {
        List<QuestionRequestDto> questions = examRequestDto.getQuestions();

        if (questions == null || questions.isEmpty()) {
            throw new CustomException("Exam must have at least one question to distribute points");
        }

        double totalAssignedPoints = 0.0;
        int questionsWithoutPoints = 0;

        for (QuestionRequestDto question : questions) {
            if (question.getPoints() != null && question.getPoints() > 0) {
                totalAssignedPoints += question.getPoints();
            } else {
                questionsWithoutPoints++;
            }
        }

        if (totalAssignedPoints > MAX_EXAM_POINTS) {
            throw new CustomException(
                    String.format("Total assigned points (%.2f) exceed maximum allowed (%.2f)",
                            totalAssignedPoints, MAX_EXAM_POINTS)
            );
        }

        if (questionsWithoutPoints == 0 && Math.abs(totalAssignedPoints - MAX_EXAM_POINTS) < 0.01) {
            log.debug("All questions have points assigned totaling {}", totalAssignedPoints);
            return;
        }

        double remainingPoints = MAX_EXAM_POINTS - totalAssignedPoints;
        if (questionsWithoutPoints > 0) {
            double pointsPerQuestion = remainingPoints / questionsWithoutPoints;
            BigDecimal bdPointsPerQuestion = BigDecimal.valueOf(pointsPerQuestion)
                    .setScale(DECIMAL_SCALE, RoundingMode.DOWN);

            double distributedPoints = 0.0;
            int processedQuestions = 0;

            for (QuestionRequestDto question : questions) {
                if (question.getPoints() == null || question.getPoints() <= 0) {
                    processedQuestions++;

                    if (processedQuestions == questionsWithoutPoints) {
                        double lastQuestionPoints = remainingPoints - distributedPoints;
                        question.setPoints(round(lastQuestionPoints));
                    } else {
                        question.setPoints(bdPointsPerQuestion.doubleValue());
                        distributedPoints += bdPointsPerQuestion.doubleValue();
                    }
                }
            }
        } else if (Math.abs(totalAssignedPoints - MAX_EXAM_POINTS) > 0.01) {
            throw new CustomException(
                    String.format("All questions have assigned points (%.2f) but don't sum to %.2f",
                            totalAssignedPoints, MAX_EXAM_POINTS)
            );
        }

        for (QuestionRequestDto question : questions) {
            distributeQuestionPoints(question);
        }

        validateTotalPoints(questions);
    }

    public void distributeQuestionPoints(QuestionRequestDto questionRequestDto) {
        List<OptionRequestDto> options = questionRequestDto.getOptions();

        if (options == null || options.isEmpty()) {
            throw new CustomException("Question must have options to distribute points");
        }

        if (questionRequestDto.getPoints() == null || questionRequestDto.getPoints() <= 0) {
            throw new CustomException("Question must have points assigned before distributing to options");
        }

        double maxQuestionPoints = questionRequestDto.getPoints();

        double totalAssignedPoints = 0.0;
        int optionsWithoutPoints = 0;

        for (OptionRequestDto option : options) {
            if (option.getPoints() != null && option.getPoints() > 0) {
                totalAssignedPoints += option.getPoints();
            } else {
                optionsWithoutPoints++;
            }
        }

        if (totalAssignedPoints > maxQuestionPoints) {
            throw new CustomException(
                    String.format("Total assigned option points (%.2f) exceed question points (%.2f)",
                            totalAssignedPoints, maxQuestionPoints)
            );
        }

        if (optionsWithoutPoints == 0 && Math.abs(totalAssignedPoints - maxQuestionPoints) < 0.01) {
            log.debug("All options have points assigned totaling {}", totalAssignedPoints);
            return;
        }

        double remainingPoints = maxQuestionPoints - totalAssignedPoints;

        if (optionsWithoutPoints > 0) {
            double pointsPerOption = remainingPoints / optionsWithoutPoints;
            BigDecimal bdPointsPerOption = BigDecimal.valueOf(pointsPerOption)
                    .setScale(DECIMAL_SCALE, RoundingMode.DOWN);

            double distributedPoints = 0.0;
            int processedOptions = 0;

            for (OptionRequestDto option : options) {
                if (option.getPoints() == null || option.getPoints() <= 0) {
                    processedOptions++;

                    if (processedOptions == optionsWithoutPoints) {
                        double lastOptionPoints = remainingPoints - distributedPoints;
                        option.setPoints(round(lastOptionPoints));
                    } else {
                        option.setPoints(bdPointsPerOption.doubleValue());
                        distributedPoints += bdPointsPerOption.doubleValue();
                    }
                }
            }
        } else if (Math.abs(totalAssignedPoints - maxQuestionPoints) > 0.01) {
            throw new CustomException(
                    String.format("All options have assigned points (%.2f) but don't sum to question points (%.2f)",
                            totalAssignedPoints, maxQuestionPoints)
            );
        }

        validateOptionPoints(options, maxQuestionPoints);
    }

    private void validateTotalPoints(List<QuestionRequestDto> questions) {
        double totalPoints = questions.stream()
                .mapToDouble(q -> q.getPoints() != null ? q.getPoints() : 0.0)
                .sum();

        if (Math.abs(totalPoints - MAX_EXAM_POINTS) > 0.01) {
            throw new CustomException(
                    String.format("Total exam points (%.2f) must equal %.2f",
                            totalPoints, MAX_EXAM_POINTS)
            );
        }
    }

    private void validateOptionPoints(List<OptionRequestDto> options, double questionPoints) {
        double totalPoints = options.stream()
                .mapToDouble(o -> o.getPoints() != null ? o.getPoints() : 0.0)
                .sum();

        if (Math.abs(totalPoints - questionPoints) > 0.01) {
            throw new CustomException(
                    String.format("Total option points (%.2f) must equal question points (%.2f)",
                            totalPoints, questionPoints)
            );
        }
    }

    private double round(double value) {
        return BigDecimal.valueOf(value)
                .setScale(DECIMAL_SCALE, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
package com.eximia.exams.mesagging.consumer;

import com.eximia.exams.dto.request.ExamRequestDto;
import com.eximia.exams.service.ExamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExamMessageConsumer {

    private final ExamService examService;

    @RabbitListener(queues = "exam.request.queue")
    public void handleExamCreation(ExamRequestDto examRequestDto) {
        try {
            log.info("Received exam creation message for title: {}", examRequestDto.getTitle());

            examService.createExam(examRequestDto);

            log.info("Successfully processed exam creation for title: {}", examRequestDto.getTitle());
        } catch (Exception e) {
            log.error("Failed to process exam creation message: {}", e.getMessage(), e);
            throw e;
        }
    }
}

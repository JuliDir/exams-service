package com.eximia.exams.mesagging.publisher;

import com.eximia.exams.config.RabbitConfig;
import com.eximia.exams.dto.request.ExamRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamMessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishExamCreation(ExamRequestDto examRequestDto) {
        try {
            log.info("Publishing exam creation message for title: {}", examRequestDto.getTitle());

            rabbitTemplate.convertAndSend(
                    RabbitConfig.EXAM_EXCHANGE,
                    RabbitConfig.EXAM_ROUTING_KEY,
                    examRequestDto
            );

            log.info("Successfully published exam creation message for title: {}", examRequestDto.getTitle());
        } catch (Exception e) {
            log.error("Failed to publish exam creation message: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to publish message", e);
        }
    }
}

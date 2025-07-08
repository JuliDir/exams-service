package com.eximia.exams.service;

import com.eximia.exams.dto.request.ExamRequestDto;
import com.eximia.exams.dto.request.QuestionRequestDto;

public interface PointsDistributionService {

    void distributeExamPoints(ExamRequestDto examRequestDto);

    void distributeQuestionPoints(QuestionRequestDto questionRequestDto);

}

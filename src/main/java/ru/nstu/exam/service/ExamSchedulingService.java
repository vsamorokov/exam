package ru.nstu.exam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExamSchedulingService {
    private final ExamService examService;

    @Value("${exam.auto-update-enabled:false}")
    private boolean updateEnabled;

    @Scheduled(fixedDelay = 5000) // 5 sec
    private void updateStates() {
        if (updateEnabled) {
            examService.updateExamStates();
        }
    }
}

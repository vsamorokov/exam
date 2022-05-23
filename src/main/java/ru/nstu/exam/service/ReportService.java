package ru.nstu.exam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nstu.exam.entity.Exam;

@Service
@RequiredArgsConstructor
public class ReportService {
    public void generateReport(Exam saved) {

    }

    public boolean hasReport(Exam exam) {
        return true;
    }
}

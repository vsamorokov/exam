package ru.nstu.exam.bean;

import lombok.Data;
import ru.nstu.exam.enums.ExamPeriodState;

@Data
public class UpdateExamPeriodBean {
    private Long start;
    private ExamPeriodState state;
}

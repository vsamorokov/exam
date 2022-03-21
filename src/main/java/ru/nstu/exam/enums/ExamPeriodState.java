package ru.nstu.exam.enums;


import lombok.RequiredArgsConstructor;
import ru.nstu.exam.entity.ExamPeriod;

@RequiredArgsConstructor
public enum ExamPeriodState {
    REDACTION(0),
    ALLOWANCE(1),
    READY(2),
    PROGRESS(3),
    FINISHED(4),
    CLOSED(5);

    private final int order;

    public boolean isAllowed(ExamPeriod examPeriod, ExamPeriodState nextState) {
        return (examPeriod.getState().order + 1) % 6 == nextState.order;
    }
}

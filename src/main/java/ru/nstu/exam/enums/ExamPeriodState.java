package ru.nstu.exam.enums;


import lombok.RequiredArgsConstructor;
import ru.nstu.exam.entity.ExamPeriod;

import java.util.Collection;

@RequiredArgsConstructor
public enum ExamPeriodState {
    /**
     * Teacher can modify period
     */
    REDACTION(0),
    /**
     * Teacher can modify allowance
     */
    ALLOWANCE(1),
    /**
     * Exam is shown to students
     */
    READY(2),
    /**
     * Students can see tasks and answer
     */
    PROGRESS(3),
    /**
     * Students cannot answer. Teacher can still rate answers
     */
    FINISHED(4),
    /**
     * Teacher cannot rate anymore
     */
    CLOSED(5);

    private final int order;

    public boolean isAllowed(ExamPeriod examPeriod) {
        return examPeriod.getState().order + 1 == this.order;
    }

    public boolean isBefore(ExamPeriodState state) {
        return this.order < state.order;
    }

    public boolean in(Collection<ExamPeriodState> states) {
        return states.contains(this);
    }

}

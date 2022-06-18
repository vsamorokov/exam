package ru.nstu.exam.enums;


import liquibase.repackaged.org.apache.commons.collections4.SetUtils;
import ru.nstu.exam.entity.Exam;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public enum ExamState {
    REDACTION(1),
    READY(2),
    TIME_SET(1, 3),
    PROGRESS(4),
    FINISHED(3, 5),
    CLOSED(),
    ;

    private final Set<Integer> allowedNext;

    ExamState(Integer... allowedNext) {
        this.allowedNext = SetUtils.hashSet(allowedNext);
    }

    public boolean isAllowedFor(Exam exam) {
        return exam.getState().allowedNext.contains(this.ordinal());
    }

    public boolean isBefore(ExamState state) {
        return this.ordinal() < state.ordinal();
    }

    public boolean in(Collection<ExamState> states) {
        return states.contains(this);
    }

    public boolean in(ExamState... states) {
        return Arrays.asList(states).contains(this);
    }

}

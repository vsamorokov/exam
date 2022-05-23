package ru.nstu.exam.enums;

import liquibase.repackaged.org.apache.commons.collections4.SetUtils;
import ru.nstu.exam.entity.StudentRating;

import java.util.Arrays;
import java.util.Set;

public enum StudentRatingState {
    EMPTY(1),
    NOT_ALLOWED(2),
    ALLOWED(1, 3),
    ASSIGNED_TO_EXAM(2, 4),
    WAITING_TO_APPEAR(5, 6),
    ABSENT(2),
    PASSING(7),
    FINISHED(8),
    RATED(2, 6),
    ;

    private final Set<Integer> allowedNext;

    StudentRatingState(Integer... allowedNext) {
        this.allowedNext = SetUtils.hashSet(allowedNext);
    }

    public boolean allowedFor(StudentRating studentRating) {
        return studentRating.getStudentRatingState().allowedNext.contains(this.ordinal());
    }


    public boolean in(StudentRatingState... states) {
        return Arrays.asList(states).contains(this);
    }

}

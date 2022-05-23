package ru.nstu.exam.enums;

import liquibase.repackaged.org.apache.commons.collections4.SetUtils;
import ru.nstu.exam.entity.Answer;

import java.util.Set;

public enum AnswerState {
    NO_ANSWER(1),
    IN_PROGRESS(2, 5),
    SENT(3, 5),
    CHECKING(1, 4, 5),
    RATED(1),
    NO_RATING(),
    ;

    private final Set<Integer> allowedNext;

    AnswerState(Integer... allowedNext) {
        this.allowedNext = SetUtils.hashSet(allowedNext);
    }

    public boolean allowedFor(Answer answer) {
        return answer.getState().allowedNext.contains(this.ordinal());
    }

    public boolean in(AnswerState... states) {
        return SetUtils.hashSet(states).contains(this);
    }
}

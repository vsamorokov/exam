package ru.nstu.exam.service.listener;

import ru.nstu.exam.entity.Answer;

public interface AnswerStateChangeListener {
    void answerStateChanged(Answer answer);
}

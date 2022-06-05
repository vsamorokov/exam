package ru.nstu.exam.service.listener;

import ru.nstu.exam.entity.Exam;

public interface ExamStateChangeListener {
    default void examCreated(Exam exam) {

    }

    default void examReady(Exam exam) {

    }

    default void examTimeSet(Exam exam) {

    }

    default void examStarted(Exam exam) {

    }

    default void examFinished(Exam saved) {

    }

    default void examClosed(Exam saved) {

    }

    default void examDeleted(Exam exam) {

    }
}

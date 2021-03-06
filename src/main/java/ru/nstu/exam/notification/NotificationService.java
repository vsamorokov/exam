package ru.nstu.exam.notification;

import ru.nstu.exam.entity.Message;
import ru.nstu.exam.service.listener.AnswerStateChangeListener;
import ru.nstu.exam.service.listener.ExamStateChangeListener;

public interface NotificationService extends ExamStateChangeListener, AnswerStateChangeListener {
    void newMessage(Message message);
}

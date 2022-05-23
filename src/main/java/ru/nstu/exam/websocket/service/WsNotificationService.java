package ru.nstu.exam.websocket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nstu.exam.entity.Answer;
import ru.nstu.exam.entity.Exam;
import ru.nstu.exam.entity.Message;

@Service
@RequiredArgsConstructor
public class WsNotificationService implements NotificationService {
    @Override
    public void examCreated(Exam exam) {

    }

    @Override
    public void examReady(Exam exam) {

    }

    @Override
    public void examStarted(Exam exam) {

    }

    @Override
    public void examFinished(Exam saved) {

    }

    @Override
    public void examClosed(Exam saved) {

    }

    @Override
    public void newMessage(Message message) {

    }

    @Override
    public void answerStateChanged(Answer answer) {

    }
}

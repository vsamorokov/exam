package ru.nstu.exam.notification.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.AnswerBean;
import ru.nstu.exam.bean.ExamBean;
import ru.nstu.exam.bean.MessageBean;
import ru.nstu.exam.entity.*;
import ru.nstu.exam.enums.NotificationType;
import ru.nstu.exam.notification.NotificationService;

import static ru.nstu.exam.utils.Utils.toMillis;

@Service
@RequiredArgsConstructor
public class WsNotificationService implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void examCreated(Exam exam) {
        sendExamNotification(exam, NotificationType.EXAM_CREATED);
    }

    @Override
    public void examReady(Exam exam) {
        sendExamNotification(exam, NotificationType.EXAM_READY);
    }

    @Override
    public void examStarted(Exam exam) {
        sendExamNotification(exam, NotificationType.EXAM_STARTED);
    }

    @Override
    public void examFinished(Exam exam) {
        sendExamNotification(exam, NotificationType.EXAM_FINISHED);
    }

    @Override
    public void examClosed(Exam exam) {
        sendExamNotification(exam, NotificationType.EXAM_CLOSED);
    }

    private void sendExamNotification(Exam exam, NotificationType type) {

        ExamBean examBean = new ExamBean();
        examBean.setId(exam.getId());
        examBean.setOneGroup(exam.isOneGroup());
        examBean.setGroupId(exam.getGroup() == null ? null : exam.getGroup().getId());
        examBean.setName(exam.getName());
        examBean.setState(exam.getState());
        examBean.setDisciplineId(exam.getDiscipline().getId());
        examBean.setStart(toMillis(exam.getStart()));
        examBean.setEnd(toMillis(exam.getEnd()));

        WsNotification wsNotification = new WsNotification();
        wsNotification.setTime(System.currentTimeMillis());
        wsNotification.setData(examBean);
        wsNotification.setType(type);

        messagingTemplate.convertAndSendToUser(String.valueOf(exam.getTeacher().getAccount().getId()), "/notifications", wsNotification);

        for (StudentRating studentRating : exam.getStudentRatings()) {
            messagingTemplate.convertAndSendToUser(String.valueOf(studentRating.getStudent().getAccount().getId()), "/notifications", wsNotification);
        }
    }

    @Override
    public void newMessage(Message message) {
        StudentRating studentRating = message.getAnswer().getStudentRating();
        Teacher teacher = studentRating.getExam().getTeacher();
        Student student = studentRating.getStudent();

        MessageBean messageBean = new MessageBean();
        messageBean.setId(message.getId());
        messageBean.setAccountId(message.getAccount().getId());
        messageBean.setSendTime(toMillis(message.getSendTime()));
        messageBean.setText(message.getText());
        messageBean.setArtefactId(message.getArtefact() == null ? null : message.getArtefact().getId());

        WsNotification wsNotification = new WsNotification();
        wsNotification.setTime(System.currentTimeMillis());
        wsNotification.setData(messageBean);
        wsNotification.setType(NotificationType.NEW_MESSAGE);

        messagingTemplate.convertAndSendToUser(String.valueOf(teacher.getAccount().getId()), "/notifications", wsNotification);
        messagingTemplate.convertAndSendToUser(String.valueOf(student.getAccount().getId()), "/notifications", wsNotification);
    }

    @Override
    public void answerStateChanged(Answer answer) {
        StudentRating studentRating = answer.getStudentRating();
        Teacher teacher = studentRating.getExam().getTeacher();
        Student student = studentRating.getStudent();

        AnswerBean answerBean = new AnswerBean();
        answerBean.setId(answer.getId());
        answerBean.setState(answer.getState());
        answerBean.setRating(answer.getRating());
        answerBean.setNumber(answer.getNumber());
        answerBean.setTaskId(answer.getTask().getId());
        answerBean.setStudentRatingId(answer.getStudentRating().getId());

        WsNotification wsNotification = new WsNotification();
        wsNotification.setTime(System.currentTimeMillis());
        wsNotification.setData(answerBean);
        wsNotification.setType(NotificationType.ANSWER_CHANGED);
        messagingTemplate.convertAndSendToUser(String.valueOf(teacher.getAccount().getId()), "/notifications", wsNotification);
        messagingTemplate.convertAndSendToUser(String.valueOf(student.getAccount().getId()), "/notifications", wsNotification);
    }
}

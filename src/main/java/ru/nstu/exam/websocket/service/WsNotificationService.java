package ru.nstu.exam.websocket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.ExamBean;
import ru.nstu.exam.entity.Answer;
import ru.nstu.exam.entity.Exam;
import ru.nstu.exam.entity.Message;
import ru.nstu.exam.entity.StudentRating;
import ru.nstu.exam.enums.NotificationType;
import ru.nstu.exam.service.NotificationService;
import ru.nstu.exam.websocket.Notification;

import static ru.nstu.exam.utils.Utils.toMillis;

@Service
@RequiredArgsConstructor
public class WsNotificationService implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

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

        for (StudentRating studentRating : exam.getStudentRatings()) {
            Long id = studentRating.getStudent().getAccount().getId();
            if (id != null) {

                Notification notification = new Notification();
                notification.setTime(System.currentTimeMillis());
                notification.setData(examBean);
                notification.setReceiverId(id);
                notification.setType(type);

                messagingTemplate.convertAndSendToUser(id.toString(), "/notifications", notification);
            }
        }
    }

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

    @Override
    public void newMessage(Message message) {

    }

    @Override
    public void answerStateChanged(Answer answer) {

    }
}

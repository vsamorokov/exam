package ru.nstu.exam.notification.firebase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.StringUtils;
import ru.nstu.exam.bean.AnswerBean;
import ru.nstu.exam.bean.ExamBean;
import ru.nstu.exam.bean.MessageBean;
import ru.nstu.exam.entity.*;
import ru.nstu.exam.enums.NotificationType;
import ru.nstu.exam.notification.NotificationService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.nstu.exam.utils.Utils.toMillis;

@Slf4j
@RequiredArgsConstructor
public class PushNotificationService implements NotificationService {

    private final ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();
    private final ClientTokenRegistry clientTokenRegistry;

    @SneakyThrows
    @Override
    public void newMessage(Message message) {

        StudentRating studentRating = message.getAnswer().getStudentRating();
        Account student = studentRating.getStudent().getAccount();

        Account teacher = studentRating.getExam().getTeacher().getAccount();

        Set<Long> ids = new HashSet<>();
        ids.add(student.getId());
        ids.add(teacher.getId());
        ids.remove(message.getAccount().getId());

        MessageBean messageBean = new MessageBean();
        messageBean.setId(message.getId());
        messageBean.setAccountId(message.getAccount().getId());
        messageBean.setText(message.getText());
        messageBean.setSendTime(toMillis(message.getSendTime()));
        messageBean.setArtefactId(message.getArtefact() == null ? null : message.getArtefact().getId());
        sendNotification(NotificationType.NEW_MESSAGE, messageBean, ids);
    }

    @SneakyThrows
    @Override
    public void answerStateChanged(Answer answer) {
        StudentRating studentRating = answer.getStudentRating();
        Account student = studentRating.getStudent().getAccount();

        Account teacher = studentRating.getExam().getTeacher().getAccount();

        Set<Long> ids = new HashSet<>();
        ids.add(student.getId());
        ids.add(teacher.getId());

        AnswerBean answerBean = new AnswerBean();
        answerBean.setId(answer.getId());
        answerBean.setStudentRatingId(answer.getStudentRating().getId());
        answerBean.setTaskId(answer.getTask().getId());
        answerBean.setState(answer.getState());
        answerBean.setNumber(answer.getNumber());
        answerBean.setRating(answer.getRating());
        sendNotification(NotificationType.ANSWER_CHANGED, answerBean, ids);
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
    public void examTimeSet(Exam exam) {
        sendExamNotification(exam, NotificationType.EXAM_TIME_SET);
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

    @SneakyThrows
    public void sendExamNotification(Exam exam, NotificationType type) {
        Long teacherId = exam.getTeacher().getAccount().getId();
        Set<Long> ids = exam.getStudentRatings().stream()
                .map(StudentRating::getStudent)
                .map(Student::getAccount)
                .map(AbstractPersistable::getId)
                .collect(Collectors.toSet());
        ids.add(teacherId);
        ExamBean examBean = new ExamBean();
        examBean.setId(exam.getId());
        examBean.setStart(toMillis(exam.getStart()));
        examBean.setEnd(toMillis(exam.getEnd()));
        examBean.setState(exam.getState());
        examBean.setDisciplineId(exam.getDiscipline().getId());
        examBean.setName(exam.getName());
        examBean.setGroupId(exam.getGroup() == null ? null : exam.getGroup().getId());
        examBean.setOneGroup(exam.isOneGroup());
        examBean.setTeacherId(exam.getTeacher().getId());
        sendNotification(type, examBean, ids);
    }

    private void sendNotification(NotificationType type, Object data, Set<Long> receivers) throws JsonProcessingException {
        com.google.firebase.messaging.Message.Builder builder = com.google.firebase.messaging.Message.builder()
                .setNotification(
                        Notification.builder()
                                .setTitle(type.name())
                                .setBody(mapper.writeValueAsString(data))
                                .build()
                );

        List<ApiFuture<String>> futures = new ArrayList<>();
        for (Long receiver : receivers) {
            String token = clientTokenRegistry.getToken(receiver);
            if (StringUtils.hasText(token)) {
                log.info("Sending Firebase notification {} to Account {}", type, receiver);
                futures.add(FirebaseMessaging.getInstance().sendAsync(builder.setToken(token).build()));
            }
        }
        for (ApiFuture<String> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                log.error("Failed to send notification", e);
            }
        }
    }
}

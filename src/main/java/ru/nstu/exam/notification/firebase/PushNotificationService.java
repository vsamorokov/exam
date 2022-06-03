package ru.nstu.exam.notification.firebase;

import com.google.api.core.ApiFuture;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Notification;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.nstu.exam.entity.*;
import ru.nstu.exam.notification.NotificationService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PushNotificationService implements NotificationService {

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

        sendNotification(new FirebaseNotification("New message", message.getText(), ids));
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

        sendNotification(new FirebaseNotification("Answer update", "", ids));
    }

    @Override
    public void examCreated(Exam exam) {
        sendExamNotification(exam, String.format("Exam %s was created", exam.getName()));
    }

    @Override
    public void examReady(Exam exam) {
        sendExamNotification(exam, String.format("Exam %s is ready", exam.getName()));
    }

    @Override
    public void examStarted(Exam exam) {
        sendExamNotification(exam, String.format("Exam %s started", exam.getName()));
    }

    @Override
    public void examFinished(Exam exam) {
        sendExamNotification(exam, String.format("Exam %s finished", exam.getName()));
    }

    @Override
    public void examClosed(Exam exam) {
        sendExamNotification(exam, String.format("Exam %s closed", exam.getName()));
    }

    @SneakyThrows
    public void sendExamNotification(Exam exam, String body) {
        Long id = exam.getTeacher().getAccount().getId();
        Set<Long> ids = exam.getStudentRatings().stream()
                .map(StudentRating::getStudent)
                .map(Student::getAccount)
                .map(AbstractPersistable::getId)
                .collect(Collectors.toSet());
        ids.add(id);
        sendNotification(new FirebaseNotification("Exam changed", body, ids));
    }

    private void sendNotification(FirebaseNotification notification) throws ExecutionException, InterruptedException {
        com.google.firebase.messaging.Message.Builder builder = com.google.firebase.messaging.Message.builder()
                .setNotification(
                        Notification.builder()
                                .setTitle(notification.getTitle())
                                .setBody(notification.getBody())
                                .build()
                );

        List<ApiFuture<String>> futures = new ArrayList<>();
        for (Long receiver : notification.getReceivers()) {
            String token = clientTokenRegistry.getToken(receiver);
            if (StringUtils.hasText(token)) {
                futures.add(FirebaseMessaging.getInstance().sendAsync(builder.setToken(token).build()));
            }
        }
        for (ApiFuture<String> future : futures) {
            future.get();
        }
    }

    @Data
    @RequiredArgsConstructor
    private static class FirebaseNotification {
        private final String title;
        private final String body;
        private final Set<Long> receivers;
    }
}

package ru.nstu.exam.websocket;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.nstu.exam.websocket.bean.WSMessageBean;


/**
 * 1. Controller receives message for answers (exam-periods) through /message/answers (/message/exam-periods) topic
 * 2. Stores a message
 * 3. Sends full message to /answers/{id} (/exam-periods/{id}) topic
 * 4. Sends notifications about message to /user/{userId}/notifications
 * for all members of the answer (exam-period)
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/answer/{id}")
    public void handleAnswerMessage(@DestinationVariable Long id, @Payload WSMessageBean messageBean, Message<WSMessageBean> message) {

        log.info("MESSAGE: {}", message);
        String answerId = messageBean.getAnswerId();

        log.info("\nAnswer {}\nMessage {}\nSending notification to /user/{}/notifications\nSending message to /answer/{}",
                answerId, messageBean, messageBean.getSenderId(), answerId);
        messagingTemplate.convertAndSendToUser(
                messageBean.getSenderId(),
                "/notifications",
                new MessageAnswer("Notification message. Hello", messageBean.getSenderId()));
        messagingTemplate.convertAndSend(
                "/answers/" + answerId,
                new MessageAnswer("Answer message. Hello", messageBean.getSenderId()));
    }

    @Data
    @AllArgsConstructor
    private static class MessageAnswer {
        private String text;
        private String userId;
    }
}

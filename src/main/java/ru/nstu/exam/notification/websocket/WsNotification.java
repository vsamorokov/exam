package ru.nstu.exam.notification.websocket;

import lombok.Data;
import ru.nstu.exam.enums.NotificationType;

@Data
public class WsNotification {
    private long time;
    private NotificationType type;
    private Object data;
}

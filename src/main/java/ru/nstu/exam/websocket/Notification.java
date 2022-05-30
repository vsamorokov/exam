package ru.nstu.exam.websocket;

import lombok.Data;
import ru.nstu.exam.enums.NotificationType;

@Data
public class Notification {
    private long time;
    private NotificationType type;
    private Object data;
}

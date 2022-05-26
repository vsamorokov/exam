package ru.nstu.exam.websocket.bean;

import lombok.Data;

@Data
public class WSMessageBean {
    private String answerId;
    private String senderId;
    private String text;
}

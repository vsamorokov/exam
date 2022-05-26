package ru.nstu.exam.websocket.service;

import org.springframework.context.ApplicationListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
public class SubscribedClientsRegistry implements ApplicationListener<SessionSubscribeEvent> {


    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        Message<byte[]> message = event.getMessage();
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        System.out.println("GOT SUBSCRIBE EVENT");
        System.out.println(event.getUser());
        System.out.println(accessor.getCommand());
        System.out.println(accessor.getDestination());
        System.out.println(accessor.getSessionId());
        System.out.println(accessor.getUser());
    }

}

package it.babyloncloud.chat.controller;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import it.babyloncloud.chat.controller.service.CredentialsService;
import it.babyloncloud.chat.controller.service.MessageService;
import it.babyloncloud.chat.model.Message;
import it.babyloncloud.chat.model.User;

@Component
public class WebSocketEventListener {
	
	private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    @Autowired
    private CredentialsService credentialsService;
    @Autowired
    private MessageService messageService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        User user = (User) headerAccessor.getSessionAttributes().get("username");
        if(user != null) {
            logger.info("User Disconnected : " + user);

            Message chatMessage = new Message();
            chatMessage.setType(Message.MessageType.LEAVE);
			chatMessage.setSender(user);
			chatMessage.setTimestamp(LocalDateTime.now());
			String username = this.credentialsService.getUsernameByUserId(user.getId());
			chatMessage.setContent(username + " left!");
			this.messageService.saveMessage(chatMessage);
            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }

}

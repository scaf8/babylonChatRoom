package it.babyloncloud.chat.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import it.babyloncloud.chat.controller.service.CredentialsService;
import it.babyloncloud.chat.controller.service.MessageService;
import it.babyloncloud.chat.filter.MessageFilter;
import it.babyloncloud.chat.model.Credentials;
import it.babyloncloud.chat.model.Message;
import it.babyloncloud.chat.model.Message.MessageType;
import it.babyloncloud.chat.model.User;

@Controller
public class ChatController {
	
    @Autowired
    private MessageService messageService;
	@Autowired
	private CredentialsService credentialsService;
	@Autowired
	private MessageFilter messageFilter;
	
	@MessageMapping("/chat.sendMessage")
	@SendTo("/topic/public")
	public Message sendMessage(@Payload Message chatMessage, @Header("username") String username) {
		float probability = messageFilter.classifyMessage(chatMessage.getContent());
		if (probability < 0.5) {
            // Messaggio non offensivo
			this.saveMessage(chatMessage, username);
        } else {
            // Messaggio offensivo
        	chatMessage.setContent("<offensive message deleted>");
        	this.saveMessage(chatMessage, username);
        }
	    return chatMessage;
	}
	
	private void saveMessage(Message chatMessage, String username) {
		Credentials credentials = this.credentialsService.getCredentials(username);
		chatMessage.setSender(credentials.getUser());
		chatMessage.setTimestamp(LocalDateTime.now());
		this.messageService.saveMessage(chatMessage);
	}

	@MessageMapping("/chat.addUser")
	@SendTo("/topic/public")
	public Message addUser(@Payload String username, SimpMessageHeaderAccessor headerAccessor) {
		// Add username in web socket session
		Credentials credentials = this.credentialsService.getCredentials(username);
		if(credentials == null) {
			// Gestici il caso in cui le credenziali non esistono nel database
		}
		User sender = credentials.getUser();
		headerAccessor.getSessionAttributes().put("username", sender);
		Message chatMessage = new Message();
		chatMessage.setType(MessageType.JOIN);
		chatMessage.setSender(sender);
		chatMessage.setContent(username + " joined!");
		chatMessage.setTimestamp(LocalDateTime.now());
		this.messageService.saveMessage(chatMessage);
		return chatMessage;
	}

}

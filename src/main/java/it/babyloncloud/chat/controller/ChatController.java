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
import it.babyloncloud.chat.controller.service.UserService;
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
    private UserService userService;
	@Autowired
	private CredentialsService credentialsService;
	@Autowired
	private MessageFilter messageFilter;
	
	@MessageMapping("/chat.sendMessage")
	@SendTo("/topic/public")
	public Message sendMessage(@Payload Message chatMessage, @Header("username") String username) {
		float probability = messageFilter.classifyMessage(chatMessage.getContent());
		System.out.println(probability);
		this.saveMessage(chatMessage, username, probability);
	    return chatMessage;
	}
	
	private void saveMessage(Message chatMessage, String username, float probability) {
		User user = this.credentialsService.getCredentials(username).getUser();
		if(probability > 0.7) {		// Messaggio offensivo?
			chatMessage.setType(MessageType.STRIKE);
			int newStrikes = user.getStrikes() +1;
			user.setStrikes(newStrikes);
			if(newStrikes >= 5)
				user.setBlocked(true);
			this.userService.saveUser(user);
		}
		chatMessage.setSender(user);
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

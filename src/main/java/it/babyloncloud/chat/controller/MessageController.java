package it.babyloncloud.chat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import it.babyloncloud.chat.controller.service.MessageService;
import it.babyloncloud.chat.model.Message;

@RestController
public class MessageController {

	@Autowired
    private MessageService messageService;

    @GetMapping(value = "/rest/messages")
    public List<Message> getAllMessages() {
        return messageService.getAllMessages();
    }
}

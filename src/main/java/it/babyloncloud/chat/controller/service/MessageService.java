package it.babyloncloud.chat.controller.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.babyloncloud.chat.model.Message;
import it.babyloncloud.chat.repository.MessageRepository;

@Service
public class MessageService {

	@Autowired
	private MessageRepository messageRepository;
	
	@Transactional
	public void saveMessage(Message message) {
		this.messageRepository.save(message);
	}

	@Transactional
	public List<Message> getAllMessages() {
		List<Message> res = new ArrayList<>();
		Iterable<Message> allMessages = this.messageRepository.findAll();
		for (Message message : allMessages) {
			res.add(message);
		}
		return res;
	}
}

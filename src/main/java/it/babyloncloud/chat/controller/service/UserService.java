package it.babyloncloud.chat.controller.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.babyloncloud.chat.model.User;
import it.babyloncloud.chat.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Transactional
	public void saveUser(User user) {
		this.userRepository.save(user);
	}

}

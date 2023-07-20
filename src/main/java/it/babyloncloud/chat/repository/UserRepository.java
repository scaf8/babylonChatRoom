package it.babyloncloud.chat.repository;

import org.springframework.data.repository.CrudRepository;

import it.babyloncloud.chat.model.User;

public interface UserRepository extends CrudRepository<User, Long> {
	
}

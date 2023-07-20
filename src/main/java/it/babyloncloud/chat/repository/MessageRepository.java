package it.babyloncloud.chat.repository;

import org.springframework.data.repository.CrudRepository;

import it.babyloncloud.chat.model.Message;

public interface MessageRepository extends CrudRepository<Message, Long> {

}

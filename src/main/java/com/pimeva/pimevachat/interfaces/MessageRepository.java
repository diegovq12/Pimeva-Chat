package com.pimeva.pimevachat.interfaces;

import com.pimeva.pimevachat.modelos.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<Message, String> {
}

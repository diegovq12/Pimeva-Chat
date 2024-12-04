package com.pimeva.pimevachat.interfaces;

import com.pimeva.pimevachat.modelos.Chat;
import com.pimeva.pimevachat.modelos.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends MongoRepository<Chat, String> {
    Optional<Chat> findByParticipantsContainingAndParticipantsContaining(User user1, User user2);
    @Query("{ 'participants.username': { $all: [?0, ?1] } }")
    Optional<Chat> findByParticipantsContainingBoth(String user1Id, String user2Id);
    Optional<Chat> findByParticipants(List<User> participants);

    @Query("{ 'participants' : { $in: [?0, ?1] } }")
    Optional<Chat> findByParticipants(String user1, String user2);
}

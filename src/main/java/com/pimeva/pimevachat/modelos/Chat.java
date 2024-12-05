package com.pimeva.pimevachat.modelos;

import com.pimeva.pimevachat.interfaces.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(collection = "chats")
public class Chat {
    @Id
    private String id;
    @DBRef
    private List<User> participants = new ArrayList<>(); // Lista de usuarios que forman parte del chat
    @DBRef
    private List<Message> messages = new ArrayList<>(); // Lista de mensajes relacionados con el chat
    private LocalDateTime lastMessageTime; // Tiempo del ultimo mensaje enviado

    // Este metodo ahora buscará los objetos User por sus nombres
    public void setParticipants(String user1Id, String user2Id, UserRepository userRepository) {
        Optional<User> user1 = userRepository.findById(user1Id);
        Optional<User> user2 = userRepository.findById(user2Id);

        if (user1.isPresent() && user2.isPresent()) {
            this.participants = Arrays.asList(user1.get(), user2.get());
        } else {
            if (!user1.isPresent()) {
                throw new RuntimeException("User with ID " + user1Id + " not found.");
            }
            if (!user2.isPresent()) {
                throw new RuntimeException("User with ID " + user2Id + " not found.");
            }
        }
    }

//
//    public void setParticipants(String user1Id, String user2Id) {
//    }
}

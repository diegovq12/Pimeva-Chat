package com.pimeva.pimevachat.services;

import com.pimeva.pimevachat.exceptions.ChatNotFoundException;
import com.pimeva.pimevachat.exceptions.UserNotFoundException;
import com.pimeva.pimevachat.interfaces.ChatRepository;
import com.pimeva.pimevachat.interfaces.UserRepository;
import com.pimeva.pimevachat.modelos.Chat;
import com.pimeva.pimevachat.modelos.Message;
import com.pimeva.pimevachat.modelos.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private UserRepository userRepository;

    public Chat getOrCreateChat(String user1, String user2) throws UserNotFoundException {
        User userOne = userRepository.findByUsername(user1)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + user1));
        User userTwo = userRepository.findByUsername(user2)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + user2));

        // Lógica para encontrar o crear un chat
        return chatRepository.findByParticipantsContainingBoth(user1,user2)
                .orElseGet(() -> {
                    Chat newChat = new Chat();
                    newChat.getParticipants().add(userOne);
                    newChat.getParticipants().add(userTwo);
                    return chatRepository.save(newChat);
                });
    }

    public List<Message> getChatMessages(String chatId) throws ChatNotFoundException {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found"));
        return chat.getMessages(); // Aquí aseguramos que sea una lista de mensajes
    }

}

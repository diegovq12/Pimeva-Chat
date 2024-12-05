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

    public Chat getOrCreateChatByIds(String userId1, String userId2) throws UserNotFoundException {
        // Buscar los usuarios por sus IDs
        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + userId1));
        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + userId2));

        // Buscar si ya existe un chat entre estos dos usuarios
        return chatRepository.findByParticipantsContainingBoth(userId1, userId2)
                .orElseGet(() -> {
                    Chat newChat = new Chat();
                    newChat.getParticipants().add(user1);  // Agregar el objeto User completo
                    newChat.getParticipants().add(user2);  // Agregar el objeto User completo
                    return chatRepository.save(newChat);  // Guardar el chat en la base de datos
                });
    }



    public List<Message> getChatMessages(String chatId) throws ChatNotFoundException {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found"));
        return chat.getMessages(); // Aquí aseguramos que sea una lista de mensajes
    }

    public Optional<Chat> getChatIdByParticipants(String userId1, String userId2) {
        try {
            return chatRepository.findByParticipantsContainingBoth(userId1, userId2);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener el chatId: " + e.getMessage(), e);
        }
    }




}

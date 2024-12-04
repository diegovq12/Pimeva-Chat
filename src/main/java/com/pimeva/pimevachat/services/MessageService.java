package com.pimeva.pimevachat.services;

import com.pimeva.pimevachat.exceptions.ChatNotFoundException;
import com.pimeva.pimevachat.interfaces.ChatRepository;
import com.pimeva.pimevachat.interfaces.MessageRepository;
import com.pimeva.pimevachat.modelos.Chat;
import com.pimeva.pimevachat.modelos.Message;
import com.pimeva.pimevachat.modelos.MessageStatus;
import com.pimeva.pimevachat.modelos.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private MessageRepository messageRepository;

    public Message sendMessage(String chatId, String content, String sender) throws ChatNotFoundException {
        // Encuentra el chat o lanza la excepción
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found: " + chatId));

        // Verifica si los participantes están presentes y tienen al menos dos
        List<User> participants = Optional.ofNullable(chat.getParticipants()).orElse(new ArrayList<>());
        if (participants.size() < 2) {
            throw new IllegalStateException("Chat must have at least two participants");
        }

        // Intenta encontrar el receptor
        String receiver = participants.stream()
                .filter(p -> !p.getId().equals(sender)) // Excluye al sender para encontrar al receiver
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Receiver not found"))
                .getId(); // Obtén el ID del receptor

        // Crea el nuevo mensaje
        Message message = new Message();
        message.setSenderId(sender);
        message.setReceiverId(receiver);
        message.setContent(content);
        message.setDateTime(LocalDateTime.now());
        message.setStatus(MessageStatus.SENT);
        message.setChatId(chatId);  // Asegúrate de que el mensaje tenga el chat asociado

        try {
            // Guarda el mensaje en el repositorio
            messageRepository.save(message);

            // Asocia el mensaje al chat
            chat.getMessages().add(message);

            // Guarda el chat actualizado
            chatRepository.save(chat);
        } catch (Exception e) {
            // Registra el error si ocurre un problema al guardar el mensaje o el chat
            System.out.println("Error saving message: " + e.getMessage());
            throw new RuntimeException("Error saving message to the chat.");
        }

        return message;
    }





}

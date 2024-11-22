package com.pimeva.pimevachat.controllers;

import com.pimeva.pimevachat.modelos.Message;
import com.pimeva.pimevachat.interfaces.MessageRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;


public class ChatController {
   MessageRepository messageRepository;
   public ChatController(MessageRepository messageRepository) {
       this.messageRepository = messageRepository;
   }


    //Metodo para manejar los mensajes enviados por los clientes
    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    public Message sendMessage(Message message) {
        // Establece la fecha y hora del mensaje antes de guardarlo
        message.setDateTime(LocalDateTime.now());

        // Guarda el mensaje en la base de datos
        messageRepository.save(message);
        return message;
    }
}

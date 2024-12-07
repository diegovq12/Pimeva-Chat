package com.pimeva.pimevachat.controllers;

import com.pimeva.pimevachat.exceptions.ChatNotFoundException;
import com.pimeva.pimevachat.modelos.Message;
import com.pimeva.pimevachat.modelos.MessageDTO;
import com.pimeva.pimevachat.services.ChatService;
import com.pimeva.pimevachat.services.MessageService;
import org.apache.catalina.Store;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@Controller
@CrossOrigin(origins = "http://localhost:3000")
public class WSChatsController {

    private final ChatService chatService;
    private final MessageService messageService;

    // Constructor para inyección de dependencias
    public WSChatsController(ChatService chatService, MessageService messageService) {
        this.chatService = chatService;
        this.messageService = messageService;
    }

//    @MessageMapping("/chat/{chatId}")
    @SendTo("/topic/chat/{chatId}")
    public MessageDTO getMessage(@Payload MessageDTO message, @DestinationVariable String chatId) throws ChatNotFoundException {
        System.out.println("Mensaje recibido en el chat " + chatId + ": " + message);

        // Aquí puedes usar el chatId para validar o guardar el mensaje en el chat correcto.
        Message processedMessage = messageService.add(message);

        // Convertir a DTO y retornar
        return new MessageDTO(
                processedMessage.getChatId(),
                processedMessage.getSenderId(),
                processedMessage.getReceiverId(),
                processedMessage.getContent(),
                processedMessage.getDateTime()
        );
    }

}
package com.pimeva.pimevachat.modelos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class MessageDTO {
    private String chatId;
    private String senderId;
    private String receiverId;
    private String content;
    private LocalDateTime datetime;

    @MessageMapping("/sendMessage")
    @SendTo("/topic/chat/{chatId}")
    public MessageDTO sendMessage(@Payload MessageDTO message, @DestinationVariable String chatId) {
        // Procesa el mensaje (guardarlo en la base de datos, etc.)
        return message; // Retorna el mensaje para que lo reciba el cliente
    }
}

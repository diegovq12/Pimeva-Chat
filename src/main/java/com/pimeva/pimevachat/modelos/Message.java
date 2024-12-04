package com.pimeva.pimevachat.modelos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "messages")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Message {
    @Id
    private String id;

    private String chatId;
    private String content;
    private String senderId;
    private String receiverId;
    private LocalDateTime dateTime;

    private String fileUrl; //  para mensajes multimedia
    private MessageStatus status; // Estados que puede tener el mensaje
    //"Sent", "Delivered", "Read"

}

package com.pimeva.pimevachat.controllers;

import com.pimeva.pimevachat.exceptions.ChatNotFoundException;
import com.pimeva.pimevachat.interfaces.ChatRepository;
import com.pimeva.pimevachat.interfaces.UserRepository;
import com.pimeva.pimevachat.modelos.*;
import com.pimeva.pimevachat.interfaces.MessageRepository;
import com.pimeva.pimevachat.services.ChatService;
import com.pimeva.pimevachat.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/chats")
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {
    @Autowired
    private ChatService chatService;
    @Autowired
    ChatRepository chatRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    MessageService messageService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    public ChatController(MessageRepository messageRepository) {
       this.messageRepository = messageRepository;
   }

    @GetMapping("/get-or-create")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Map<String, String>> getOrCreateChat(@RequestParam String user1, @RequestParam String user2) {
        try {
            // Intentar obtener los usuarios por nombre de usuario
            Optional<User> user1Obj = userRepository.findByUsername(user1);
            Optional<User> user2Obj = userRepository.findByUsername(user2);

            // Si no se encuentran por nombre, intentar por ID
            if (!user1Obj.isPresent()) {
                user1Obj = userRepository.findById(user1); // Intentar con ID
            }
            if (!user2Obj.isPresent()) {
                user2Obj = userRepository.findById(user2); // Intentar con ID
            }

            // Verificar que ambos usuarios existen
            if (!user1Obj.isPresent() || !user2Obj.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Usuarios no encontrados"));
            }

            // Obtener los IDs de los usuarios
            String user1Id = user1Obj.get().getId();
            String user2Id = user2Obj.get().getId();

            // Ordenar los IDs para evitar duplicados (si el orden no es importante)
            if (user1Id.compareTo(user2Id) > 0) {
                String temp = user1Id;
                user1Id = user2Id;
                user2Id = temp;
            }

            // Buscar si ya existe un chat con ambos usuarios
            Optional<Chat> existingChat = chatRepository.findByParticipantsContainingBoth(user1Id, user2Id);

            if (existingChat.isPresent()) {
                // Si ya existe un chat, devolver el chatId
                return ResponseEntity.ok(Map.of("chatId", existingChat.get().getId()));
            } else {
                // Si no existe, crear un nuevo chat
                Chat newChat = new Chat();
                newChat.setParticipants(user1Id, user2Id,userRepository);  // Solo pasamos los IDs

                // Guardar el nuevo chat en la base de datos
                Chat savedChat = chatRepository.save(newChat);

                return ResponseEntity.ok(Map.of("chatId", savedChat.getId()));
            }

        } catch (Exception e) {
            // Manejo de excepciones
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ocurrió un error en el servidor", "message", e.getMessage()));
        }
    }





    @GetMapping("/{chatId}/messages")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<List<Message>> getChatMessages(@PathVariable String chatId) throws ChatNotFoundException {
        List<Message> messages = chatService.getChatMessages(chatId);
        return ResponseEntity.ok(messages);
    }


    @MessageMapping("/chat/{chatId}")
    @SendTo("/topic/chat/{chatId}")
    @PostMapping("/sendMessage")
    @CrossOrigin(origins = "http://localhost:3000")
    public Map<String, Object> sendMessage(
            @DestinationVariable String chatId,
            @RequestBody Map<String, Object> body // Cambiar el tipo a Object
    ) throws ChatNotFoundException {
        // Extrae el contenido del mensaje
        String content = (String) body.get("content");

        // Extrae el senderId
        String sender = (String) body.get("senderId"); // Asegúrate de usar "senderId"

        // Llama al servicio para enviar el mensaje
        Message message = messageService.sendMessage(chatId, content, sender);

        // Devuelve una respuesta consistente
        Map<String, Object> response = new HashMap<>();
        response.put("id", message.getId());
        response.put("chatId", message.getChatId());
        response.put("content", message.getContent());
        response.put("senderId", message.getSenderId());
        response.put("receiverId", message.getReceiverId());
        response.put("dateTime", message.getDateTime());
        response.put("fileUrl", message.getFileUrl());
        response.put("status", message.getStatus());

        System.out.println("Mensaje enviado: " + response);
        return response;
    }

//    @MessageMapping("/chat.sendMessage") // Mensajes que llegan desde el cliente
//    @SendTo("/topic/chat/{chatId}") // Se envían a todos los suscritos a este canal
//    public Message sendMessage(@Payload Message chatMessage) {
//        // Lógica para guardar el mensaje en la base de datos, si es necesario
//        messageRepository.save(chatMessage);
//
//        return chatMessage;
//    }

//    public void sendMessageToUser(String chatId, Message message) {
//        messagingTemplate.convertAndSend("/topic/chat/" + chatId, message);
//
//    }

    @GetMapping("/getChatIdByParticipants")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Map<String, String>> getChatIdByParticipants(@RequestParam String userId1, @RequestParam String userId2) {
        try {
            // Buscar el chat utilizando los IDs de los usuarios
            Optional<Chat> chat = chatService.getChatIdByParticipants(userId1, userId2);

            if (chat.isPresent()) {
                return ResponseEntity.ok(Map.of("chatId", chat.get().getId()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Chat no encontrado"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error interno del servidor: " + e.getMessage()));
        }
    }


}

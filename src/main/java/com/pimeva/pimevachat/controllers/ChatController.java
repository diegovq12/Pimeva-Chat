package com.pimeva.pimevachat.controllers;

import com.pimeva.pimevachat.exceptions.ChatNotFoundException;
import com.pimeva.pimevachat.interfaces.ChatRepository;
import com.pimeva.pimevachat.interfaces.UserRepository;
import com.pimeva.pimevachat.modelos.Chat;
import com.pimeva.pimevachat.modelos.ChatDTO;
import com.pimeva.pimevachat.modelos.Message;
import com.pimeva.pimevachat.interfaces.MessageRepository;
import com.pimeva.pimevachat.modelos.User;
import com.pimeva.pimevachat.services.ChatService;
import com.pimeva.pimevachat.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/chats")
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
   public ChatController(MessageRepository messageRepository) {
       this.messageRepository = messageRepository;
   }

    @GetMapping("/get-or-create")
    public ResponseEntity<ChatDTO> getOrCreateChat(@RequestParam String user1, @RequestParam String user2) {
        // Obtener los usuarios por sus nombres de usuario
        Optional<User> user1Obj = userRepository.findByUsername(user1);
        Optional<User> user2Obj = userRepository.findByUsername(user2);

        // Verificar que ambos usuarios existen
        if (!user1Obj.isPresent() || !user2Obj.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // O maneja el error de otra forma
        }

        // Obtener los IDs de los usuarios
        String user1Id = user1Obj.get().getId();
        String user2Id = user2Obj.get().getId();

        // Buscar si ya existe un chat con los dos usuarios
        Optional<Chat> existingChat = chatRepository.findByParticipants(user1Id, user2Id);

        if (existingChat.isPresent()) {
            // Si ya existe un chat, devolver el chat encontrado
            return ResponseEntity.ok(new ChatDTO(existingChat.get()));
        } else {
            // Si no existe, crear un nuevo chat
            Chat newChat = new Chat();
            newChat.setParticipants(user1Id, user2Id, userRepository);  // Aquí pasas el repositorio para buscar los usuarios

            // Guardar el nuevo chat en la base de datos
            Chat savedChat = chatRepository.save(newChat);

            return ResponseEntity.ok(new ChatDTO(savedChat));
        }
    }

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<Message>> getChatMessages(@PathVariable String chatId) throws ChatNotFoundException {
        List<Message> messages = chatService.getChatMessages(chatId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/sendMessage")
    public ResponseEntity<Message> sendMessage(
            @RequestParam String chatId,
            @RequestParam String sender,
            @RequestBody Map<String, String> body // Recibe el cuerpo como un Map o un POJO
    ) throws ChatNotFoundException {

        // Extrae el contenido del mensaje desde el cuerpo
        String content = body.get("content");

        // Llama al servicio para enviar el mensaje
        Message message = messageService.sendMessage(chatId, content, sender);

        return ResponseEntity.ok(message);
    }



}

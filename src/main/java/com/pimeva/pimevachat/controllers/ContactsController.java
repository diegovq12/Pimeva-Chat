package com.pimeva.pimevachat.controllers;

import com.pimeva.pimevachat.exceptions.UserNotFoundException;
import com.pimeva.pimevachat.modelos.ContactDTO;
import com.pimeva.pimevachat.modelos.User;
import com.pimeva.pimevachat.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
@CrossOrigin(origins = "http://localhost:3000")
public class ContactsController {
    @Autowired
    private UserService userService;

    @PostMapping("/send-request")
    public ResponseEntity<String> sendContactRequest(@RequestParam String sender, @RequestParam String receiver) throws UserNotFoundException {
        userService.sendContactRequest(sender,receiver);
        return ResponseEntity.ok("Sending request to " + receiver);
    }

    @PostMapping("/accept-request")
    public ResponseEntity<String> acceptContactRequest(@RequestParam String receiver, @RequestParam String sender) throws UserNotFoundException {
        userService.acceptContactRequest(receiver, sender);
        return ResponseEntity.ok("Friend request accepted!");
    }

    @GetMapping("/get-contacts")
    public ResponseEntity<List<ContactDTO>> getContacts(@RequestParam String userId) throws UserNotFoundException {
        List<ContactDTO> contacts = userService.getContacts(userId);  // Llama al servicio
        return ResponseEntity.ok(contacts);  // Retorna los contactos en la respuesta
    }

    @GetMapping("/get-requests")
    public ResponseEntity<List<ContactDTO>> getContactsRequests(@RequestParam String userId) throws UserNotFoundException {
        List<ContactDTO> requests = userService.getContactsRequests(userId);  // Llama al servicio
        return ResponseEntity.ok(requests);  // Retorna las solicitudes en la respuesta
    }

}

package com.pimeva.pimevachat.services;

import com.pimeva.pimevachat.exceptions.UserNotFoundException;
import com.pimeva.pimevachat.modelos.ContactDTO;
import com.pimeva.pimevachat.modelos.User;
import com.pimeva.pimevachat.interfaces.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Registro de usuario
    public ResponseEntity<?> registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists!");
        }

        user.setProfilePicture("https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png");
        user.setOnline(false);
        user.setContacts(new ArrayList<>());
        user.setContactRequests(new ArrayList<>());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully!");
    }

    public ResponseEntity<?> loginUser(User user) {
        // Buscar usuario por username
        Optional<User> optionalUser = userRepository.findByUsername(user.getUsername());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("incorrect username or password");
        }

        User existingUser = optionalUser.get();
        if (!passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("incorrect username or password");
        }
        return ResponseEntity.ok("Login successful!");
    }


    @Transactional
    public void sendContactRequest(String sender, String receiver) throws UserNotFoundException {
        User senderEntity = userRepository.findByUsername(sender)
                .orElseThrow(() -> new UserNotFoundException("Sender not found: " + sender));
        User receiverEntity = userRepository.findByUsername(receiver)
                .orElseThrow(() -> new UserNotFoundException("Receiver not found: " + receiver));

        // Evita solicitudes duplicadas
        if (!receiverEntity.getContactRequests().contains(senderEntity)) {
            receiverEntity.getContactRequests().add(senderEntity);
            userRepository.save(receiverEntity); // Solo se guarda el receptor
        }
    }

    @Transactional
    public void acceptContactRequest(String sender, String receiver) throws UserNotFoundException {
        User senderEntity = userRepository.findByUsername(sender)
                .orElseThrow(() -> new UserNotFoundException("Sender not found: " + sender));
        User receiverEntity = userRepository.findByUsername(receiver)
                .orElseThrow(() -> new UserNotFoundException("Receiver not found: " + receiver));

        // Verifica que la solicitud exista antes de aceptarla
        if (receiverEntity.getContactRequests().contains(senderEntity)) {
            // Agrega a la lista de contactos
            senderEntity.getContacts().add(receiverEntity);
            receiverEntity.getContacts().add(senderEntity);

            // Elimina la solicitud de contacto
            receiverEntity.getContactRequests().remove(senderEntity);

            // Guarda ambas entidades
            userRepository.save(senderEntity);
            userRepository.save(receiverEntity);
        }
    }

    public List<ContactDTO> getContacts(String userId) throws UserNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        // Mapear los contactos de User a ContactDTO
        return user.getContacts().stream()
                .map(contact -> new ContactDTO(contact.getId(), contact.getUsername(), contact.getProfilePicture()))
                .collect(Collectors.toList());
    }

    public List<ContactDTO>getContactsRequests(String userId) throws UserNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        return user.getContactRequests().stream()
                .map(contact -> new ContactDTO(contact.getId(), contact.getUsername(), contact.getProfilePicture()))
                .collect(Collectors.toList());
    }

    public String getUserIdByUsername(String username) {
        // Buscar al usuario en la base de datos por su username
        Optional<User> user = userRepository.findByUsername(username);
        if (user != null) {
            return user.get().getId();  // Retornar el userId
        }
        return null; // O manejar caso de error (usuario no encontrado)
    }
}

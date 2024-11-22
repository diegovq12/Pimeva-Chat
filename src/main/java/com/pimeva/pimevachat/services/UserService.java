package com.pimeva.pimevachat.services;

import com.pimeva.pimevachat.modelos.User;
import com.pimeva.pimevachat.interfaces.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Registro de usuario
    public ResponseEntity<?> registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists!");
        }

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully!");
    }

    public ResponseEntity<?> loginUser(User user) {
        // Buscar usuario por username
        Optional<User> optionalUser = userRepository.findByUsername(user.getUsername());

        User existingUser = optionalUser.get();
        // Comparar contraseñas
        if (!existingUser.getPassword().equals(user.getPassword())) {
            // Contraseña incorrecta
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Contraseña o usuario incorrectos");
        }

        // Login exitoso
        return ResponseEntity.ok("Login exitoso");
    }

}

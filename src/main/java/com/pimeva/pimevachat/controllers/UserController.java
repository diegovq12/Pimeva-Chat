package com.pimeva.pimevachat.controllers;

import com.pimeva.pimevachat.interfaces.UserRepository;
import com.pimeva.pimevachat.modelos.User;
import com.pimeva.pimevachat.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    @Autowired
    private UserRepository userRepository;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register") // Esta sería la ruta /api/users/register
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        userService.registerUser(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login") // Esta sería la ruta /api/users/login
    public ResponseEntity<?> loginUser(@RequestBody User user) {
       return userService.loginUser(user);
    }

    @GetMapping("/check-username")
    public ResponseEntity<Optional<User>> checkUsername(@RequestParam String username) {
        Optional<User> exists = userRepository.findByUsername(username);
        System.out.println("Usuario existe?"+exists);
        return ResponseEntity.ok(exists);  // Retorna true si el usuario ya existe
    }

}

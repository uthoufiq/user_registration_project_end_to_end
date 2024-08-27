package com.noobmaster.registration.controller;

import com.noobmaster.registration.model.User;
import com.noobmaster.registration.repository.UserRepository;
import com.noobmaster.registration.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequestMapping("/")
@Controller
public class RegistrationController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;

    private static final Logger log = LoggerFactory.getLogger(RegistrationController.class);

    @PostMapping("/register")
    public ResponseEntity<String> submitForm(@RequestBody User user) {
        log.info("Received registration request for user: " + user.getUsername());
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            log.warn("User already exists: " + user.getUsername());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists.");
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            log.info("User registered successfully: " + user.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body("Your registration was successful!");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User user) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent() && passwordEncoder.matches(user.getPassword(), existingUser.get().getPassword())) {
            log.info("Login successful: " + user.getUsername());
            String token = jwtUtil.generateToken(user.getUsername());
            return ResponseEntity.status(HttpStatus.OK).body("Bearer " + token);
        } else {
            log.info("Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }
}
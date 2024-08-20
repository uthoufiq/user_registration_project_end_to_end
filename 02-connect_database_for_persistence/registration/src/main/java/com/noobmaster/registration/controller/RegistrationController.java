package com.noobmaster.registration.controller;

import com.noobmaster.registration.model.User;
import com.noobmaster.registration.repository.UserRepository;
import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@RequestMapping("/")
@Controller
public class RegistrationController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String showForm(Model model) {
        model.addAttribute("user", new User());
        return "index";
    }

    @PostMapping("/register")
    public String submitForm(@ModelAttribute("user") User user, Model model) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            model.addAttribute("message", "The " + user.getUsername() + ", already exists");
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            model.addAttribute("message", "Hi " + user.getUsername() + ", your registration was successful!!");
        }
        return "result";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());
        return "login";  // Render the login page
    }

    @PostMapping("/login1")
    public String loginForm(@ModelAttribute("user") User user, Model model) {
        try {
            Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
            if (existingUser.isPresent() && passwordEncoder.matches(user.getPassword(), existingUser.get().getPassword())) {
                model.addAttribute("user", existingUser.get());
                return "dashboard";
            }
        } catch (Exception e) {
            model.addAttribute("error", "An error occured during login. Please try again.");
            return "login1";
        }
        return null;
    }
}
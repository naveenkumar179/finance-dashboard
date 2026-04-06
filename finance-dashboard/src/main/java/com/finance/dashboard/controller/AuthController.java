package com.finance.dashboard.controller;

import com.finance.dashboard.model.*;
import com.finance.dashboard.repository.UserRepository;
import com.finance.dashboard.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password){

        User user = repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(user.getStatus() == Status.INACTIVE){
            throw new RuntimeException("User inactive");
        }

        if(!encoder.matches(password, user.getPassword())){
            throw new RuntimeException("Invalid password");
        }

        return jwt.generateToken(user.getEmail(), user.getRole().name());
    }
}
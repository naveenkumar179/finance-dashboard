package com.finance.dashboard.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.finance.dashboard.model.*;
import com.finance.dashboard.repository.UserRepository;
import com.finance.dashboard.view.Views;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @JsonView(Views.Internal.class)
    public User create(@JsonView(Views.Internal.class) @Valid @RequestBody User user){
        user.setId(null); 
        user.setPassword(encoder.encode(user.getPassword()));
        return repo.save(user);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @JsonView(Views.Internal.class)
    public List<User> getAll(){
        return repo.findAll();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public User updateStatus(@PathVariable Long id, @RequestParam Status status) {
        User user = repo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(status);
        return repo.save(user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @JsonView(Views.Internal.class)
    public User update(@PathVariable Long id, @JsonView(Views.Internal.class) @Valid @RequestBody User user) {
        User u = repo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        u.setName(user.getName());
        u.setEmail(user.getEmail());
        u.setRole(user.getRole());
        u.setStatus(user.getStatus());
        // Only hash and update password if it's provided as a non-empty string
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            u.setPassword(encoder.encode(user.getPassword()));
        }
        return repo.save(u);
    }
}
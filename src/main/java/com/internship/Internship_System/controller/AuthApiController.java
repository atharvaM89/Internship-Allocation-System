package com.internship.Internship_System.controller;

import com.internship.Internship_System.dto.RegisterRequest;
import com.internship.Internship_System.model.Role;
import com.internship.Internship_System.model.User;
import com.internship.Internship_System.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public AuthApiController(UserRepository userRepository,
                             BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()) != null) {
            return ResponseEntity.badRequest()
                    .body("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole(request.getRole());


        if (request.getRole() == Role.STUDENT) {
            user.setCgpa(request.getCgpa());
            user.setBranch(request.getBranch());
            user.setSkills(request.getSkills());
        }

        if (request.getRole() == Role.COMPANY) {
            user.setApproved(false);
        }

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }
}

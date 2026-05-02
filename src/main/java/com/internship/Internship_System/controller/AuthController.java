package com.internship.Internship_System.controller;

import com.internship.Internship_System.model.Role;
import com.internship.Internship_System.model.User;
import com.internship.Internship_System.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserService userService;
    private final com.internship.Internship_System.repository.UserRepository userRepository;
    private final org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder;

    public AuthController(UserService userService,
                          com.internship.Internship_System.repository.UserRepository userRepository,
                          org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    // ---------------- LOGIN PAGE ----------------
    @GetMapping("/")
    public String loginPage(
            @RequestParam(required = false) String expired,
            Model model
    ) {

        if ("true".equals(expired)) {
            model.addAttribute(
                    "error",
                    "Session expired. Please login again."
            );
        }

        return "login";
    }

    // ---------------- LOGIN PROCESS ----------------
    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        User user = userService.login(email, password);

        if (user == null) {
            model.addAttribute(
                    "error",
                    "Invalid email or password"
            );
            return "login";
        }


        session.setAttribute("user", user);


        if (user.getRole() == Role.STUDENT) {
            return "redirect:/student/dashboard";
        }

        if (user.getRole() == Role.COMPANY) {
            return "redirect:/company/dashboard";
        }

        return "redirect:/admin/dashboard";
    }

    // ---------------- LOGOUT ----------------
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // destroy session
        return "redirect:/";
    }

    // ---------------- SIGNUP PAGES ----------------
    @GetMapping("/signup/student")
    public String signupStudentPage(Model model) {
        model.addAttribute("role", Role.STUDENT);
        return "signup-student";
    }

    @GetMapping("/signup/company")
    public String signupCompanyPage(Model model) {
        model.addAttribute("role", Role.COMPANY);
        return "signup-company";
    }

    @GetMapping("/signup/admin")
    public String signupAdminPage(Model model) {
        model.addAttribute("role", Role.ADMIN);
        return "signup-admin";
    }

    // ---------------- SIGNUP PROCESS ----------------
    @PostMapping("/signup")
    public String signup(@ModelAttribute User user,
                         @RequestParam Role role,
                         Model model) {

        if (userRepository.findByEmail(user.getEmail()) != null) {
            model.addAttribute("error", "Email already exists");
            return "signup-" + role.name().toLowerCase();
        }

        user.setRole(role);
        user.setPassword(encoder.encode(user.getPassword()));

        if (role == Role.COMPANY) {
            user.setApproved(false);
        }

        userRepository.save(user);

        model.addAttribute("success", "Registration successful! Please login.");
        return "login";
    }
}


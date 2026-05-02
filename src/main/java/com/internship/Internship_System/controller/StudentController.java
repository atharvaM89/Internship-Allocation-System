package com.internship.Internship_System.controller;

import com.internship.Internship_System.model.ApplicationStatus;
import com.internship.Internship_System.model.Role;
import com.internship.Internship_System.model.User;
import com.internship.Internship_System.repository.ApplicationRepository;
import com.internship.Internship_System.repository.NotificationRepository;
import com.internship.Internship_System.repository.UserRepository;
import com.internship.Internship_System.service.InternshipService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final InternshipService internshipService;
    private final BCryptPasswordEncoder encoder;

    public StudentController(ApplicationRepository applicationRepository,
                             UserRepository userRepository,
                             NotificationRepository notificationRepository,
                             InternshipService internshipService,
                             BCryptPasswordEncoder encoder) {

        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.internshipService = internshipService;
        this.encoder = encoder;
    }

    // ---------------- DASHBOARD ----------------
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        User student = (User) session.getAttribute("user");
        if (student == null) return "redirect:/";

        int total = applicationRepository.findByStudent(student).size();
        int selected = applicationRepository
                .findByStudentAndStatus(student, ApplicationStatus.SELECTED)
                .size();

        int profileFilled = 0;
        if (student.getCgpa() != null) profileFilled += 33;
        if (student.getBranch() != null) profileFilled += 33;
        if (student.getSkills() != null && !student.getSkills().isEmpty()) profileFilled += 34;

        model.addAttribute("total", total);
        model.addAttribute("selected", selected);
        model.addAttribute("profileStatus", profileFilled + "%");

        return "student/student-dashboard";
    }

    // ---------------- PROFILE ----------------
    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {

        User student = (User) session.getAttribute("user");
        if (student == null || student.getRole() != Role.STUDENT) {
            return "redirect:/?expired=true";
        }

        int completion = 0;
        if (student.getCgpa() != null) completion += 25;
        if (student.getBranch() != null) completion += 25;
        if (student.getSkills() != null && !student.getSkills().isBlank()) completion += 50;

        model.addAttribute("student", student);
        model.addAttribute("completion", completion);

        return "student/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute User updated,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        User student = (User) session.getAttribute("user");
        if (student == null || student.getRole() != Role.STUDENT) {
            return "redirect:/?expired=true";
        }

        student.setCgpa(updated.getCgpa());
        student.setBranch(updated.getBranch());
        student.setSkills(updated.getSkills());

        userRepository.save(student);
        session.setAttribute("user", student);

        redirectAttributes.addFlashAttribute(
                "success",
                "Profile updated successfully"
        );

        return "redirect:/student/profile";
    }

    // ---------------- NOTIFICATIONS ----------------
    @GetMapping("/notifications")
    public String notifications(HttpSession session, Model model) {

        User student = (User) session.getAttribute("user");
        if (student == null) return "redirect:/";

        model.addAttribute(
                "notifications",
                notificationRepository.findByUserOrderByIdDesc(student)
        );

        return "student/notifications";
    }

    // ---------------- CHANGE PASSWORD ----------------
    @GetMapping("/change-password")
    public String changePasswordPage(HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/";
        return "student/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 HttpSession session,
                                 Model model) {

        User student = (User) session.getAttribute("user");
        if (student == null) return "redirect:/";

        if (!encoder.matches(oldPassword, student.getPassword())) {
            model.addAttribute("error", "Old password is incorrect");
            return "student/change-password";
        }

        student.setPassword(encoder.encode(newPassword));
        userRepository.save(student);

        model.addAttribute("success", "Password changed successfully");
        return "student/change-password";
    }

    // ---------------- APPLICATIONS ----------------
    @GetMapping("/applications")
    public String applications(HttpSession session, Model model) {

        User student = (User) session.getAttribute("user");
        if (student == null) return "redirect:/";

        model.addAttribute(
                "applications",
                applicationRepository.findByStudent(student)
        );

        return "student/applications";
    }

    // ---------------- SELECTED ----------------
    @GetMapping("/selected")
    public String selected(HttpSession session, Model model) {

        User student = (User) session.getAttribute("user");
        if (student == null) return "redirect:/";

        model.addAttribute(
                "applications",
                applicationRepository.findByStudentAndStatus(
                        student,
                        ApplicationStatus.SELECTED
                )
        );

        return "student/selected";
    }

    // ---------------- REJECTED ----------------
    @GetMapping("/rejected")
    public String rejected(HttpSession session, Model model) {

        User student = (User) session.getAttribute("user");
        if (student == null) return "redirect:/";

        model.addAttribute(
                "applications",
                applicationRepository.findByStudentAndStatus(
                        student,
                        ApplicationStatus.REJECTED
                )
        );

        return "student/rejected";
    }

    // ---------------- ANALYTICS ----------------
    @GetMapping("/analytics")
    public String analytics(HttpSession session, Model model) {

        User student = (User) session.getAttribute("user");
        if (student == null) return "redirect:/";

        long applied = applicationRepository
                .findByStudentAndStatusIn(
                        student,
                        List.of(ApplicationStatus.APPLIED, ApplicationStatus.WITHDRAWN)
                ).size();

        long selected = applicationRepository
                .findByStudentAndStatus(student, ApplicationStatus.SELECTED)
                .size();

        long rejected = applicationRepository
                .findByStudentAndStatus(student, ApplicationStatus.REJECTED)
                .size();

        model.addAttribute("applied", applied);
        model.addAttribute("selected", selected);
        model.addAttribute("rejected", rejected);

        return "student/analytics";
    }

    // ---------------- RECOMMENDATIONS ----------------
    @GetMapping("/recommendations")
    public String recommendations(HttpSession session, Model model) {

        User student = (User) session.getAttribute("user");
        if (student == null) return "redirect:/";

        model.addAttribute(
                "internships",
                internshipService.getRecommended(student)
        );

        return "student/recommendations";
    }
}

package com.internship.Internship_System.controller;

import com.internship.Internship_System.model.*;
import com.internship.Internship_System.repository.ApplicationRepository;
import com.internship.Internship_System.repository.InternshipRepository;
import com.internship.Internship_System.service.ApplicationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ApplicationController {

    private final ApplicationService applicationService;
    private final ApplicationRepository applicationRepository;
    private final InternshipRepository internshipRepository;

    public ApplicationController(ApplicationService applicationService,
                                 ApplicationRepository applicationRepository,
                                 InternshipRepository internshipRepository) {
        this.applicationService = applicationService;
        this.applicationRepository = applicationRepository;
        this.internshipRepository = internshipRepository;
    }

    // ---------------- APPLY ----------------
    @PostMapping("/applications/apply")
    public String apply(@RequestParam Long internshipId,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {

        User student = (User) session.getAttribute("user");
        if (student == null || student.getRole() != Role.STUDENT) {
            return "redirect:/";
        }

        Internship internship = internshipRepository
                .findById(internshipId)
                .orElse(null);

        if (internship == null || internship.getAvailableSlots() <= 0) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Internship not available"
            );
            return "redirect:/internships";
        }

        boolean applied = applicationService.apply(student, internship);

        if (!applied) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Already applied or not eligible"
            );
        } else {
            redirectAttributes.addFlashAttribute(
                    "success",
                    "Application submitted"
            );
        }

        return "redirect:/internships";
    }

    // ---------------- DETAILS ----------------
    @GetMapping("/student/applications/{id}")
    public String details(@PathVariable Long id,
                          HttpSession session,
                          Model model) {

        User student = (User) session.getAttribute("user");
        if (student == null) return "redirect:/";

        Application app = applicationRepository.findById(id).orElse(null);

        if (app == null || !app.getStudent().getId().equals(student.getId())) {
            return "redirect:/student/applications";
        }

        model.addAttribute("application", app);
        return "student/application-details";
    }

    // ---------------- WITHDRAW ----------------
    @PostMapping("/applications/withdraw/{id}")
    public String withdraw(@PathVariable Long id,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {

        User student = (User) session.getAttribute("user");
        if (student == null) return "redirect:/";

        Application app = applicationRepository.findById(id).orElse(null);

        if (app == null ||
                !app.getStudent().getId().equals(student.getId())) {
            return "redirect:/student/applications";
        }

        if (app.getStatus() != ApplicationStatus.APPLIED) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Cannot withdraw after allocation"
            );
            return "redirect:/student/applications";
        }

        app.setStatus(ApplicationStatus.WITHDRAWN);
        applicationRepository.save(app);

        redirectAttributes.addFlashAttribute(
                "success",
                "Application withdrawn successfully"
        );

        return "redirect:/student/applications";
    }
}
package com.internship.Internship_System.controller;

import com.internship.Internship_System.model.Application;
import com.internship.Internship_System.model.ApplicationStatus;
import com.internship.Internship_System.model.Role;
import com.internship.Internship_System.model.User;
import com.internship.Internship_System.repository.ApplicationRepository;
import com.internship.Internship_System.repository.DomainRepository;
import com.internship.Internship_System.repository.InternshipRepository;
import com.internship.Internship_System.service.ApplicationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/company")
public class CompanyController {

    private final InternshipRepository internshipRepository;
    private final ApplicationRepository applicationRepository;
    private final DomainRepository domainRepository;
    private final ApplicationService applicationService;

    public CompanyController(InternshipRepository internshipRepository,
                             ApplicationRepository applicationRepository,
                             DomainRepository domainRepository,
                             ApplicationService applicationService) {
        this.internshipRepository = internshipRepository;
        this.applicationRepository = applicationRepository;
        this.domainRepository = domainRepository;
        this.applicationService = applicationService;
    }

    // ---------------- DASHBOARD ----------------
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        User company = (User) session.getAttribute("user");
        if (company == null || company.getRole() != Role.COMPANY) {
            return "redirect:/";
        }

        var internships = internshipRepository.findAll()
                .stream()
                .filter(i -> i.getCompany() != null &&
                        i.getCompany().getId().equals(company.getId()))
                .toList();

        long totalApplications = internships.stream()
                .flatMap(i -> applicationRepository.findByInternship(i).stream())
                .count();

        long selected = internships.stream()
                .flatMap(i -> applicationRepository.findByInternship(i).stream())
                .filter(a -> a.getStatus() == ApplicationStatus.SELECTED)
                .count();

        model.addAttribute("internships", internships);
        model.addAttribute("totalInternships", internships.size());
        model.addAttribute("totalApplications", totalApplications);
        model.addAttribute("selected", selected);

        return "company/dashboard";
    }

    // ---------------- POST INTERNSHIP PAGE ----------------
    @GetMapping("/post")
    public String postInternship(HttpSession session, Model model) {

        User company = (User) session.getAttribute("user");
        if (company == null || company.getRole() != Role.COMPANY) {
            return "redirect:/";
        }

        model.addAttribute("domains", domainRepository.findAll());
        return "company/post";
    }

    // ---------------- VIEW APPLICANTS ----------------
    @GetMapping("/internship/{id}/applications")
    public String viewApplicants(@PathVariable Long id,
                                 HttpSession session,
                                 Model model) {

        User company = (User) session.getAttribute("user");
        if (company == null || company.getRole() != Role.COMPANY) {
            return "redirect:/";
        }

        var internship = internshipRepository.findById(id).orElse(null);

        if (internship == null ||
                !internship.getCompany().getId().equals(company.getId())) {
            return "redirect:/company/dashboard";
        }

        List<Application> applications =
                applicationRepository.findByInternship(internship);

        model.addAttribute("internship", internship);
        model.addAttribute("applications", applications);

        return "company/applicants";
    }

    // ---------------- APPROVE APPLICATION ----------------
    @PostMapping("/applications/{id}/approve")
    public String approve(@PathVariable Long id) {
        applicationService.updateStatus(id, ApplicationStatus.SELECTED);
        return "redirect:/company/dashboard";
    }

    // ---------------- REJECT APPLICATION ----------------
    @PostMapping("/applications/{id}/reject")
    public String reject(@PathVariable Long id) {
        applicationService.updateStatus(id, ApplicationStatus.REJECTED);
        return "redirect:/company/dashboard";
    }
}
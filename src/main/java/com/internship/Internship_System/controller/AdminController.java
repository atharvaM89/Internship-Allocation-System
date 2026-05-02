package com.internship.Internship_System.controller;

import com.internship.Internship_System.model.Role;
import com.internship.Internship_System.model.User;
import com.internship.Internship_System.repository.*;
import com.internship.Internship_System.service.AllocationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final InternshipRepository internshipRepository;
    private final ApplicationRepository applicationRepository;
    private final AllocationService allocationService;
    private final DomainRepository domainRepository;
    private final DomainRoadmapStepRepository roadmapRepository;

    public AdminController(UserRepository userRepository,
                           InternshipRepository internshipRepository,
                           ApplicationRepository applicationRepository,
                           AllocationService allocationService,
                           DomainRepository domainRepository,
                           DomainRoadmapStepRepository roadmapRepository) {

        this.userRepository = userRepository;
        this.internshipRepository = internshipRepository;
        this.applicationRepository = applicationRepository;
        this.allocationService = allocationService;
        this.domainRepository = domainRepository;
        this.roadmapRepository = roadmapRepository;
    }

    // ================= ADMIN DASHBOARD =================
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        User admin = (User) session.getAttribute("user");
        if (admin == null || admin.getRole() != Role.ADMIN) {
            return "redirect:/";
        }

        model.addAttribute(
                "companies",
                userRepository.findAll()
                        .stream()
                        .filter(u -> u.getRole() == Role.COMPANY)
                        .toList()
        );

        model.addAttribute("internships", internshipRepository.findAll());
        model.addAttribute("applications", applicationRepository.findAll());
        model.addAttribute("domains", domainRepository.findAll());

        return "admin/admin-dashboard";
    }

    // ================= APPROVE COMPANY =================
    @PostMapping("/company/{id}/approve")
    public String approveCompany(@PathVariable Long id,
                                 HttpSession session) {

        User admin = (User) session.getAttribute("user");
        if (admin == null || admin.getRole() != Role.ADMIN) {
            return "redirect:/";
        }

        userRepository.findById(id).ifPresent(company -> {
            if (company.getRole() == Role.COMPANY) {
                company.setApproved(true);
                userRepository.save(company);
            }
        });

        return "redirect:/admin/dashboard";
    }

    // ================= RUN FULL ALLOCATION =================
    @PostMapping("/internship/{id}/allocate")
    public String runAllocation(@PathVariable Long id,
                                HttpSession session) {

        User admin = (User) session.getAttribute("user");
        if (admin == null || admin.getRole() != Role.ADMIN) {
            return "redirect:/";
        }

        allocationService.allocate(id);
        return "redirect:/admin/dashboard";
    }


    @GetMapping("/internship/{id}/allocate")
    public String preventGetAllocate() {
        return "redirect:/admin/dashboard";
    }

    // ================= SELECT SINGLE STUDENT =================
    @PostMapping("/application/{id}/select")
    public String selectSingleStudent(@PathVariable Long id,
                                      HttpSession session) {

        User admin = (User) session.getAttribute("user");
        if (admin == null || admin.getRole() != Role.ADMIN) {
            return "redirect:/";
        }

        allocationService.selectSingleStudent(id);
        return "redirect:/admin/dashboard";
    }


    @GetMapping("/application/{id}/select")
    public String preventGetSelect() {
        return "redirect:/admin/dashboard";
    }
    @PostMapping("/allocate/{internshipId}")
    public String allocate(@PathVariable Long internshipId) {
        allocationService.allocate(internshipId);
        return "redirect:/admin/dashboard";
    }
}
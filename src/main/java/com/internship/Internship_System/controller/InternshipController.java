package com.internship.Internship_System.controller;

import com.internship.Internship_System.model.*;
import com.internship.Internship_System.repository.ApplicationRepository;
import com.internship.Internship_System.repository.DomainRepository;
import com.internship.Internship_System.service.InternshipService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/internships")
public class InternshipController {

    private final InternshipService internshipService;
    private final ApplicationRepository applicationRepository;
    private final DomainRepository domainRepository;

    public InternshipController(InternshipService internshipService,
                                ApplicationRepository applicationRepository,
                                DomainRepository domainRepository) {
        this.internshipService = internshipService;
        this.applicationRepository = applicationRepository;
        this.domainRepository = domainRepository;
    }

    // STUDENT : LIST ALL INTERNSHIPS

    @GetMapping
    public String list(@RequestParam(required = false) String skill,
                       Model model,
                       HttpSession session) {

        User student = (User) session.getAttribute("user");
        if (student == null || student.getRole() != Role.STUDENT) {
            return "redirect:/";
        }

        List<Internship> internships = internshipService.getAll();

        if (skill != null && !skill.isEmpty()) {
            internships = internships.stream()
                    .filter(i -> i.getRequiredSkills() != null &&
                            i.getRequiredSkills().toLowerCase()
                                    .contains(skill.toLowerCase()))
                    .toList();
        }

        model.addAttribute("internships", internships);
        return "student/internships";
    }

    // STUDENT : INTERNSHIP DETAILS

    @GetMapping("/{id}")
    public String details(@PathVariable Long id,
                          HttpSession session,
                          Model model) {

        User student = (User) session.getAttribute("user");
        if (student == null || student.getRole() != Role.STUDENT) {
            return "redirect:/";
        }

        Internship internship = internshipService.getById(id);

        boolean alreadyApplied =
                applicationRepository.existsByStudentAndInternship(student, internship);

        boolean eligible = isEligible(student, internship);

        model.addAttribute("internship", internship);
        model.addAttribute("alreadyApplied", alreadyApplied);
        model.addAttribute("eligible", eligible);

        return "student/internship-details";
    }

    // STUDENT : VIEW DOMAIN ROADMAP

    @GetMapping("/{id}/roadmap")
    public String roadmap(@PathVariable Long id,
                          HttpSession session,
                          Model model) {

        User student = (User) session.getAttribute("user");
        if (student == null || student.getRole() != Role.STUDENT) {
            return "redirect:/";
        }

        Internship internship = internshipService.getById(id);

        model.addAttribute("internship", internship);
        model.addAttribute(
                "roadmap",
                internship.getDomain().getRoadmapSteps()
        );

        return "student/roadmap";
    }

    // COMPANY : POST INTERNSHIP

    @PostMapping("/add")
    public String add(@RequestParam String title,
                      @RequestParam String description,
                      @RequestParam Double minCgpa,
                      @RequestParam String requiredSkills,
                      @RequestParam String allowedBranch,
                      @RequestParam Integer totalSlots,
                      @RequestParam Long domainId,
                      HttpSession session,
                      RedirectAttributes redirectAttributes) {

        User company = (User) session.getAttribute("user");

        if (company == null || company.getRole() != Role.COMPANY) {
            return "redirect:/";
        }

        if (!company.getApproved()) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Admin approval required before posting internships"
            );
            return "redirect:/company/dashboard";
        }

        if (totalSlots == null || totalSlots <= 0) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Total slots must be greater than 0"
            );
            return "redirect:/company/post";
        }

        Domain domain = domainRepository.findById(domainId).orElse(null);
        if (domain == null) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Invalid domain selected"
            );
            return "redirect:/company/post";
        }

        Internship internship = new Internship();
        internship.setTitle(title);
        internship.setDescription(description);
        internship.setMinCgpa(minCgpa);
        internship.setRequiredSkills(requiredSkills);
        internship.setAllowedBranch(allowedBranch);
        internship.setTotalSlots(totalSlots);
        internship.setAvailableSlots(totalSlots);
        internship.setCompany(company);
        internship.setDomain(domain);

        internshipService.save(internship);

        redirectAttributes.addFlashAttribute(
                "success",
                "Internship posted successfully"
        );

        return "redirect:/company/dashboard";
    }

    // ELIGIBILITY LOGIC

    private boolean isEligible(User student, Internship internship) {

        if (student.getCgpa() == null ||
                student.getBranch() == null ||
                student.getSkills() == null) {
            return false;
        }

        if (student.getCgpa() < internship.getMinCgpa()) return false;
        if (!student.getBranch().equalsIgnoreCase(internship.getAllowedBranch())) return false;
        if (!student.getSkills().contains(internship.getRequiredSkills())) return false;

        return internship.getAvailableSlots() > 0;
    }
}


package com.internship.Internship_System.service;

import com.internship.Internship_System.model.Internship;
import com.internship.Internship_System.model.User;
import com.internship.Internship_System.repository.InternshipRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InternshipService {

    private final InternshipRepository internshipRepository;

    public InternshipService(InternshipRepository internshipRepository) {
        this.internshipRepository = internshipRepository;
    }

    public Internship save(Internship internship) {
        return internshipRepository.save(internship);
    }

    public List<Internship> getAll() {
        return internshipRepository.findAll();
    }

    public Internship getById(Long id) {
        return internshipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Internship not found"));
    }

    /**
     * Recommended internships for student
     */
    public List<Internship> getRecommended(User student) {

        if (student.getCgpa() == null ||
                student.getBranch() == null ||
                student.getSkills() == null) {
            return List.of();
        }

        return internshipRepository.findAll()
                .stream()
                .filter(i -> student.getCgpa() >= i.getMinCgpa())
                .filter(i -> student.getBranch().equalsIgnoreCase(i.getAllowedBranch()))
                .filter(i -> student.getSkills().contains(i.getRequiredSkills()))
                .filter(i -> i.getAvailableSlots() > 0)
                .toList();
    }
}
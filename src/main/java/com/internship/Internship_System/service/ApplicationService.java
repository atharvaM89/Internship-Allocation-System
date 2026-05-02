package com.internship.Internship_System.service;

import com.internship.Internship_System.model.*;
import com.internship.Internship_System.repository.ApplicationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    public List<Application> getApplicationsForCompany(Long companyId) {
        return applicationRepository.findAll().stream()
                .filter(a -> a.getInternship().getCompany().getId().equals(companyId))
                .toList();
    }
    public void updateStatus(Long applicationId, ApplicationStatus status) {
        Application application = applicationRepository.findById(applicationId).orElseThrow();
        application.setStatus(status);
        applicationRepository.save(application);
    }
    /**
     * Apply for an internship
     */
    public boolean apply(User student, Internship internship) {

        // Duplicate application check
        if (applicationRepository.existsByStudentAndInternship(student, internship)) {
            return false;
        }
        if (internship.getDomain() == null ||
                internship.getDomain().getRoadmapSteps() == null ||
                internship.getDomain().getRoadmapSteps().isEmpty()) {
            return false;
        }

        // Null safety
        if (student.getCgpa() == null ||
                student.getBranch() == null ||
                student.getSkills() == null) {
            return false;
        }

        // Eligibility checks
        if (student.getCgpa() < internship.getMinCgpa()) {
            return false;
        }

        if (!student.getBranch().equalsIgnoreCase(internship.getAllowedBranch())) {
            return false;
        }

        if (!student.getSkills().contains(internship.getRequiredSkills())) {
            return false;
        }

        // Create application
        Application application = new Application();
        application.setStudent(student);
        application.setInternship(internship);
        application.setStatus(ApplicationStatus.APPLIED);

        int score = (int) (student.getCgpa() * 10);
        application.setScore(score);

        applicationRepository.save(application);
        return true;
    }
}


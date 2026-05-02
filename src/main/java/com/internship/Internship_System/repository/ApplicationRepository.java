package com.internship.Internship_System.repository;

import com.internship.Internship_System.model.Application;
import com.internship.Internship_System.model.ApplicationStatus;
import com.internship.Internship_System.model.Internship;
import com.internship.Internship_System.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // All applications for ranking
    List<Application> findByInternshipOrderByScoreDesc(Internship internship);

    // Student applications
    List<Application> findByStudent(User student);

    // Selected / Rejected
    List<Application> findByStudentAndStatus(User student, ApplicationStatus status);

    // Duplicate prevention
    boolean existsByStudentAndInternship(User student, Internship internship);

    List<Application> findByStudentAndStatusIn(User student, List<ApplicationStatus> statuses);

    List<Application> findByInternship(Internship internship);

    List<Application> findByInternshipCompany(User company);
}
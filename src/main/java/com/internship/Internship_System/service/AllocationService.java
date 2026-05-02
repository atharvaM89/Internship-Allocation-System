package com.internship.Internship_System.service;

import com.internship.Internship_System.model.*;
import com.internship.Internship_System.repository.ApplicationRepository;
import com.internship.Internship_System.repository.InternshipRepository;
import com.internship.Internship_System.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AllocationService {

    private final ApplicationRepository applicationRepository;
    private final InternshipRepository internshipRepository;
    private final NotificationRepository notificationRepository;

    public AllocationService(ApplicationRepository applicationRepository,
                             InternshipRepository internshipRepository,
                             NotificationRepository notificationRepository) {
        this.applicationRepository = applicationRepository;
        this.internshipRepository = internshipRepository;
        this.notificationRepository = notificationRepository;
    }



    @Transactional
    public void allocate(Long internshipId) {

        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("Internship not found"));

        if (internship.getAvailableSlots() <= 0) {
            return;
        }

        List<Application> applications =
                applicationRepository.findByInternshipOrderByScoreDesc(internship);

        int remainingSlots = internship.getAvailableSlots();

        for (Application app : applications) {


            if (app.getStatus() != ApplicationStatus.APPLIED) {
                continue;
            }

            Notification notification = new Notification();
            notification.setUser(app.getStudent());

            if (remainingSlots > 0) {
                app.setStatus(ApplicationStatus.SELECTED);
                remainingSlots--;

                notification.setMessage(
                        "You are SELECTED for internship: " +
                                internship.getTitle()
                );
            } else {
                app.setStatus(ApplicationStatus.REJECTED);

                notification.setMessage(
                        " You were NOT selected for internship: " +
                                internship.getTitle()
                );
            }

            applicationRepository.save(app);
            notificationRepository.save(notification);
        }

        internship.setAvailableSlots(remainingSlots);
        internshipRepository.save(internship);
    }



    @Transactional
    public void selectSingleStudent(Long applicationId) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        Internship internship = application.getInternship();

        // Safety checks
        if (application.getStatus() != ApplicationStatus.APPLIED) return;
        if (internship.getAvailableSlots() <= 0) return;

        // Select student
        application.setStatus(ApplicationStatus.SELECTED);
        applicationRepository.save(application);


        internship.setAvailableSlots(internship.getAvailableSlots() - 1);
        internshipRepository.save(internship);

        // Notify selected student
        Notification success = new Notification();
        success.setUser(application.getStudent());
        success.setMessage(
                " You are SELECTED for internship: " +
                        internship.getTitle()
        );
        notificationRepository.save(success);


        if (internship.getAvailableSlots() == 0) {

            List<Application> others =
                    applicationRepository.findByInternship(internship);

            for (Application other : others) {
                if (other.getStatus() == ApplicationStatus.APPLIED) {
                    other.setStatus(ApplicationStatus.REJECTED);
                    applicationRepository.save(other);

                    Notification reject = new Notification();
                    reject.setUser(other.getStudent());
                    reject.setMessage(
                            " You were NOT selected for internship: " +
                                    internship.getTitle()
                    );
                    notificationRepository.save(reject);
                }
            }
        }
    }
}
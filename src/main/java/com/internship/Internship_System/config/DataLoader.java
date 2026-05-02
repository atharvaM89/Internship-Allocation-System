package com.internship.Internship_System.config;

import com.internship.Internship_System.model.Domain;
import com.internship.Internship_System.model.DomainRoadmapStep;
import com.internship.Internship_System.repository.DomainRepository;
import com.internship.Internship_System.repository.DomainRoadmapStepRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Profile("dev")
@Configuration
public class DataLoader {

    @Bean
    @Transactional
    CommandLineRunner loadDomainsAndRoadmaps(
            DomainRepository domainRepository,
            DomainRoadmapStepRepository roadmapRepository
    ) {
        return args -> {

            if (domainRepository.count() > 0) {
                return;
            }

            createDomain(
                    domainRepository,
                    roadmapRepository,
                    "Java Full Stack",
                    "Complete Java + Backend + Frontend stack",
                    List.of(
                            "Java Basics",
                            "Core Java",
                            "Git & Version Control",
                            "Servlets & JSP",
                            "Postman & REST",
                            "Spring Core",
                            "Spring Boot",
                            "Spring Data JPA",
                            "Spring Security",
                            "Microservices",
                            "Deployment (Docker, AWS)"
                    )
            );

            createDomain(
                    domainRepository,
                    roadmapRepository,
                    "Android Development",
                    "Android apps using Java/Kotlin",
                    List.of(
                            "Java/Kotlin Basics",
                            "Android Studio",
                            "Activities & Fragments",
                            "Layouts & UI",
                            "Room Database",
                            "REST APIs",
                            "Firebase",
                            "Play Store Deployment"
                    )
            );

            createDomain(
                    domainRepository,
                    roadmapRepository,
                    "AI / ML",
                    "Artificial Intelligence & Machine Learning",
                    List.of(
                            "Python Basics",
                            "NumPy & Pandas",
                            "Data Preprocessing",
                            "Machine Learning Algorithms",
                            "Model Evaluation",
                            "Deep Learning Basics",
                            "Project & Deployment"
                    )
            );

            createDomain(
                    domainRepository,
                    roadmapRepository,
                    "Cyber Security",
                    "Security, ethical hacking, networks",
                    List.of(
                            "Networking Basics",
                            "Linux Fundamentals",
                            "Cyber Laws",
                            "Vulnerabilities",
                            "Ethical Hacking",
                            "Penetration Testing",
                            "Security Tools"
                    )
            );

            System.out.println(" Domains and Roadmaps loaded successfully (DEV)");
        };
    }

    private void createDomain(
            DomainRepository domainRepository,
            DomainRoadmapStepRepository roadmapRepository,
            String name,
            String description,
            List<String> steps
    ) {
        Domain domain = new Domain();
        domain.setName(name);
        domain.setDescription(description);
        domainRepository.save(domain);

        int order = 1;
        for (String title : steps) {
            DomainRoadmapStep step = new DomainRoadmapStep();
            step.setDomain(domain);
            step.setStepOrder(order++);
            step.setTitle(title);
            step.setDescription("Learn " + title + " in detail");
            roadmapRepository.save(step);
        }
    }
}
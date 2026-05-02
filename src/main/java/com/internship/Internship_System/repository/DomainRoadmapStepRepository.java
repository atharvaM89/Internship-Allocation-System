package com.internship.Internship_System.repository;

import com.internship.Internship_System.model.Domain;
import com.internship.Internship_System.model.DomainRoadmapStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DomainRoadmapStepRepository
        extends JpaRepository<DomainRoadmapStep, Long> {

    List<DomainRoadmapStep> findByDomainOrderByStepOrder(Domain domain);
}
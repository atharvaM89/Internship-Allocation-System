package com.internship.Internship_System.repository;

import com.internship.Internship_System.model.Domain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DomainRepository extends JpaRepository<Domain, Long> {
    Domain findByName(String name);
}
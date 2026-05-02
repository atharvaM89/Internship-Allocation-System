package com.internship.Internship_System.service;

import com.internship.Internship_System.model.Domain;
import com.internship.Internship_System.repository.DomainRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DomainService {

    private final DomainRepository domainRepository;

    public DomainService(DomainRepository domainRepository) {
        this.domainRepository = domainRepository;
    }

    public Domain getOrCreate(String name, String description) {
        Domain domain = domainRepository.findByName(name);
        if (domain == null) {
            domain = new Domain();
            domain.setName(name);
            domain.setDescription(description);
            domainRepository.save(domain);
        }
        return domain;
    }

    public List<Domain> getAll() {
        return domainRepository.findAll();
    }
}
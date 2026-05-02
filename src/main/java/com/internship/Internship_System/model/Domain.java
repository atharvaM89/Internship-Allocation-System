package com.internship.Internship_System.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Domain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    @OneToMany(mappedBy = "domain", cascade = CascadeType.ALL)
    @OrderBy("stepOrder ASC")
    private List<DomainRoadmapStep> roadmapSteps;

    // getters & setters
    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<DomainRoadmapStep> getRoadmapSteps() { return roadmapSteps; }
    public void setRoadmapSteps(List<DomainRoadmapStep> roadmapSteps) {
        this.roadmapSteps = roadmapSteps;
    }
}
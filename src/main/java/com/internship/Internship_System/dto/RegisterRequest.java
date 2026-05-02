package com.internship.Internship_System.dto;

import com.internship.Internship_System.model.Role;

public class RegisterRequest {

    private String name;
    private String email;
    private String password;
    private Role role;

    // Student fields
    private Double cgpa;
    private String branch;
    private String skills;

    // getters & setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Double getCgpa() { return cgpa; }
    public void setCgpa(Double cgpa) { this.cgpa = cgpa; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
}
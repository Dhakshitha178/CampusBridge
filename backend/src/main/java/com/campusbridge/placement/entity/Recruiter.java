package com.campusbridge.placement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recruiter_profiles")
public class Recruiter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    private String designation;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Recruiter() {}

    public Recruiter(User user, Company company, String designation) {
        this.user = user;
        this.company = company;
        this.designation = designation;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getFullName() { return user != null ? user.getFullName() : ""; }
    public String getEmail() { return user != null ? user.getEmail() : ""; }
}

package com.campusbridge.placement.entity;

import com.campusbridge.placement.entity.enums.TargetAudience;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "announcements")
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "college_id", nullable = false)
    private College college;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_audience", nullable = false)
    private TargetAudience targetAudience = TargetAudience.ALL;

    @Column(name = "target_department")
    private String targetDepartment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(name = "posted_by")
    private String postedBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Announcement() {}

    public Announcement(College college, String title, String content, TargetAudience targetAudience, String targetDepartment, Company company, String postedBy) {
        this.college = college;
        this.title = title;
        this.content = content;
        this.targetAudience = targetAudience;
        this.targetDepartment = targetDepartment;
        this.company = company;
        this.postedBy = postedBy;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public College getCollege() { return college; }
    public void setCollege(College college) { this.college = college; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public TargetAudience getTargetAudience() { return targetAudience; }
    public void setTargetAudience(TargetAudience targetAudience) { this.targetAudience = targetAudience; }
    public String getTargetDepartment() { return targetDepartment; }
    public void setTargetDepartment(String targetDepartment) { this.targetDepartment = targetDepartment; }
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    public String getPostedBy() { return postedBy; }
    public void setPostedBy(String postedBy) { this.postedBy = postedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

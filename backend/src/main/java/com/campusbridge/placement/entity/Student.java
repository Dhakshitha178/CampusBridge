package com.campusbridge.placement.entity;

import com.campusbridge.placement.entity.enums.PlacementStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_profiles")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "college_id", nullable = false)
    private College college;

    @Column(name = "roll_number", nullable = false, unique = true)
    private String rollNumber;

    @Column(nullable = false)
    private String department;

    @Column(name = "academic_year", nullable = false, length = 50)
    private String academicYear;

    @Column(name = "graduation_year", nullable = false)
    private Integer graduationYear;

    @Column(nullable = false)
    private Double cgpa;

    @Column(nullable = false)
    private Integer backlogs = 0;

    @Column(columnDefinition = "TEXT")
    private String skills;

    @Column(columnDefinition = "TEXT")
    private String certifications;

    @Column(name = "resume_url", length = 500)
    private String resumeUrl;

    @Column(name = "resume_file_name")
    private String resumeFileName;

    @Enumerated(EnumType.STRING)
    @Column(name = "placement_status")
    private PlacementStatus placementStatus = PlacementStatus.UNPLACED;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Student() {}

    public Student(User user, College college, String rollNumber, String department, String academicYear, Integer graduationYear, Double cgpa, Integer backlogs, String skills) {
        this.user = user;
        this.college = college;
        this.rollNumber = rollNumber;
        this.department = department;
        this.academicYear = academicYear;
        this.graduationYear = graduationYear;
        this.cgpa = cgpa;
        this.backlogs = backlogs;
        this.skills = skills;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public College getCollege() { return college; }
    public void setCollege(College college) { this.college = college; }
    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    public Integer getGraduationYear() { return graduationYear; }
    public void setGraduationYear(Integer graduationYear) { this.graduationYear = graduationYear; }
    public Double getCgpa() { return cgpa; }
    public void setCgpa(Double cgpa) { this.cgpa = cgpa; }
    public Integer getBacklogs() { return backlogs; }
    public void setBacklogs(Integer backlogs) { this.backlogs = backlogs; }
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    public String getCertifications() { return certifications; }
    public void setCertifications(String certifications) { this.certifications = certifications; }
    public String getResumeUrl() { return resumeUrl; }
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }
    public String getResumeFileName() { return resumeFileName; }
    public void setResumeFileName(String resumeFileName) { this.resumeFileName = resumeFileName; }
    public PlacementStatus getPlacementStatus() { return placementStatus; }
    public void setPlacementStatus(PlacementStatus placementStatus) { this.placementStatus = placementStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Convenience delegates to underlying User
    public String getFullName() { return user != null ? user.getFullName() : ""; }
    public String getEmail() { return user != null ? user.getEmail() : ""; }
}
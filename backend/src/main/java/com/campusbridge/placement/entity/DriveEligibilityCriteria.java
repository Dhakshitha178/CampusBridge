package com.campusbridge.placement.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "drive_eligibility_criteria")
public class DriveEligibilityCriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drive_id", nullable = false, unique = true)
    @JsonBackReference
    private PlacementDrive drive;

    @Column(name = "allowed_departments", columnDefinition = "TEXT")
    private String allowedDepartments; // Comma-separated: "CSE,IT,ECE"

    @Column(name = "min_cgpa", nullable = false)
    private Double minCgpa;

    @Column(name = "max_backlogs", nullable = false)
    private Integer maxBacklogs;

    @Column(name = "graduation_year", nullable = false)
    private Integer graduationYear;

    @Column(name = "required_skills", columnDefinition = "TEXT")
    private String requiredSkills; // Comma-separated: "Java,SQL,React"

    public DriveEligibilityCriteria() {}

    public DriveEligibilityCriteria(PlacementDrive drive, String allowedDepartments, Double minCgpa, Integer maxBacklogs, Integer graduationYear, String requiredSkills) {
        this.drive = drive;
        this.allowedDepartments = allowedDepartments;
        this.minCgpa = minCgpa;
        this.maxBacklogs = maxBacklogs;
        this.graduationYear = graduationYear;
        this.requiredSkills = requiredSkills;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public PlacementDrive getDrive() { return drive; }
    public void setDrive(PlacementDrive drive) { this.drive = drive; }
    public String getAllowedDepartments() { return allowedDepartments; }
    public void setAllowedDepartments(String allowedDepartments) { this.allowedDepartments = allowedDepartments; }
    public Double getMinCgpa() { return minCgpa; }
    public void setMinCgpa(Double minCgpa) { this.minCgpa = minCgpa; }
    public Integer getMaxBacklogs() { return maxBacklogs; }
    public void setMaxBacklogs(Integer maxBacklogs) { this.maxBacklogs = maxBacklogs; }
    public Integer getGraduationYear() { return graduationYear; }
    public void setGraduationYear(Integer graduationYear) { this.graduationYear = graduationYear; }
    public String getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(String requiredSkills) { this.requiredSkills = requiredSkills; }
}

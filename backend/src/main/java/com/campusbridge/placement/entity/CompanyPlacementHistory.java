package com.campusbridge.placement.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "company_placement_history")
public class CompanyPlacementHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "college_id", nullable = false)
    private College college;

    @Column(name = "academic_year", nullable = false, length = 50)
    private String academicYear;

    @Column(name = "applicant_count")
    private Integer applicantCount = 0;

    @Column(name = "shortlisted_count")
    private Integer shortlistedCount = 0;

    @Column(name = "selected_count")
    private Integer selectedCount = 0;

    @Column(name = "roles_offered")
    private String rolesOffered;

    @Column(name = "avg_package")
    private Double avgPackage = 0.0;

    @Column(name = "highest_package")
    private Double highestPackage = 0.0;

    public CompanyPlacementHistory() {}

    public CompanyPlacementHistory(Company company, College college, String academicYear, Integer applicantCount, Integer shortlistedCount, Integer selectedCount, String rolesOffered, Double avgPackage, Double highestPackage) {
        this.company = company;
        this.college = college;
        this.academicYear = academicYear;
        this.applicantCount = applicantCount;
        this.shortlistedCount = shortlistedCount;
        this.selectedCount = selectedCount;
        this.rolesOffered = rolesOffered;
        this.avgPackage = avgPackage;
        this.highestPackage = highestPackage;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    public College getCollege() { return college; }
    public void setCollege(College college) { this.college = college; }
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    public Integer getApplicantCount() { return applicantCount; }
    public void setApplicantCount(Integer applicantCount) { this.applicantCount = applicantCount; }
    public Integer getShortlistedCount() { return shortlistedCount; }
    public void setShortlistedCount(Integer shortlistedCount) { this.shortlistedCount = shortlistedCount; }
    public Integer getSelectedCount() { return selectedCount; }
    public void setSelectedCount(Integer selectedCount) { this.selectedCount = selectedCount; }
    public String getRolesOffered() { return rolesOffered; }
    public void setRolesOffered(String rolesOffered) { this.rolesOffered = rolesOffered; }
    public Double getAvgPackage() { return avgPackage; }
    public void setAvgPackage(Double avgPackage) { this.avgPackage = avgPackage; }
    public Double getHighestPackage() { return highestPackage; }
    public void setHighestPackage(Double highestPackage) { this.highestPackage = highestPackage; }
}

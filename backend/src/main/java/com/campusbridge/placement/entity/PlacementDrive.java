package com.campusbridge.placement.entity;

import com.campusbridge.placement.entity.enums.DriveStatus;
import com.campusbridge.placement.entity.enums.EmploymentType;
import com.campusbridge.placement.entity.enums.WorkMode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "placement_drives")
public class PlacementDrive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "college_id")
    private College college;

    @Column(name = "job_title", nullable = false)
    private String jobTitle;

    @Column(name = "job_description", columnDefinition = "TEXT", nullable = false)
    private String jobDescription;

    @Column(name = "package_lpa", nullable = false)
    private Double packageLpa;

    @Column(nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_mode")
    private WorkMode workMode = WorkMode.ON_SITE;

    @Column(nullable = false)
    private Integer vacancies;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type")
    private EmploymentType employmentType = EmploymentType.FULL_TIME;

    @Column(name = "application_deadline", nullable = false)
    private LocalDateTime applicationDeadline;

    @Column(name = "drive_date", nullable = false)
    private LocalDate driveDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DriveStatus status = DriveStatus.PENDING_APPROVAL;

    @Column(name = "tpo_approval_notes", columnDefinition = "TEXT")
    private String tpoApprovalNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User createdBy;

    @OneToOne(mappedBy = "drive", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private DriveEligibilityCriteria eligibilityCriteria;

    @OneToMany(mappedBy = "drive", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("stageOrder ASC")
    @JsonManagedReference
    private List<RecruitmentStage> recruitmentStages = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public PlacementDrive() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    public College getCollege() { return college; }
    public void setCollege(College college) { this.college = college; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getJobDescription() { return jobDescription; }
    public void setJobDescription(String jobDescription) { this.jobDescription = jobDescription; }
    public Double getPackageLpa() { return packageLpa; }
    public void setPackageLpa(Double packageLpa) { this.packageLpa = packageLpa; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public WorkMode getWorkMode() { return workMode; }
    public void setWorkMode(WorkMode workMode) { this.workMode = workMode; }
    public Integer getVacancies() { return vacancies; }
    public void setVacancies(Integer vacancies) { this.vacancies = vacancies; }
    public EmploymentType getEmploymentType() { return employmentType; }
    public void setEmploymentType(EmploymentType employmentType) { this.employmentType = employmentType; }
    public LocalDateTime getApplicationDeadline() { return applicationDeadline; }
    public void setApplicationDeadline(LocalDateTime applicationDeadline) { this.applicationDeadline = applicationDeadline; }
    public LocalDate getDriveDate() { return driveDate; }
    public void setDriveDate(LocalDate driveDate) { this.driveDate = driveDate; }
    public DriveStatus getStatus() { return status; }
    public void setStatus(DriveStatus status) { this.status = status; }
    public String getTpoApprovalNotes() { return tpoApprovalNotes; }
    public void setTpoApprovalNotes(String tpoApprovalNotes) { this.tpoApprovalNotes = tpoApprovalNotes; }
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
    public DriveEligibilityCriteria getEligibilityCriteria() { return eligibilityCriteria; }
    public void setEligibilityCriteria(DriveEligibilityCriteria eligibilityCriteria) { this.eligibilityCriteria = eligibilityCriteria; }
    public List<RecruitmentStage> getRecruitmentStages() { return recruitmentStages; }
    public void setRecruitmentStages(List<RecruitmentStage> recruitmentStages) { this.recruitmentStages = recruitmentStages; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

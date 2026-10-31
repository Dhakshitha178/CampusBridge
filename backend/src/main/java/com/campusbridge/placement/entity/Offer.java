package com.campusbridge.placement.entity;

import com.campusbridge.placement.entity.enums.OfferStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "offers")
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "application_id", nullable = false, unique = true)
    private Application application;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "drive_id", nullable = false)
    private PlacementDrive drive;

    @Column(name = "job_role", nullable = false)
    private String jobRole;

    @Column(name = "package_lpa", nullable = false)
    private Double packageLpa;

    @Column(nullable = false)
    private String location;

    @Column(name = "joining_date")
    private LocalDate joiningDate;

    @Column(name = "offer_letter_url", length = 500)
    private String offerLetterUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "offer_status", nullable = false)
    private OfferStatus offerStatus = OfferStatus.OFFERED;

    @Column(name = "offered_at")
    private LocalDateTime offeredAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Offer() {}

    public Offer(Application application, Student student, Company company, PlacementDrive drive, String jobRole, Double packageLpa, String location, LocalDate joiningDate) {
        this.application = application;
        this.student = student;
        this.company = company;
        this.drive = drive;
        this.jobRole = jobRole;
        this.packageLpa = packageLpa;
        this.location = location;
        this.joiningDate = joiningDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Application getApplication() { return application; }
    public void setApplication(Application application) { this.application = application; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    public PlacementDrive getDrive() { return drive; }
    public void setDrive(PlacementDrive drive) { this.drive = drive; }
    public String getJobRole() { return jobRole; }
    public void setJobRole(String jobRole) { this.jobRole = jobRole; }
    public Double getPackageLpa() { return packageLpa; }
    public void setPackageLpa(Double packageLpa) { this.packageLpa = packageLpa; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public LocalDate getJoiningDate() { return joiningDate; }
    public void setJoiningDate(LocalDate joiningDate) { this.joiningDate = joiningDate; }
    public String getOfferLetterUrl() { return offerLetterUrl; }
    public void setOfferLetterUrl(String offerLetterUrl) { this.offerLetterUrl = offerLetterUrl; }
    public OfferStatus getOfferStatus() { return offerStatus; }
    public void setOfferStatus(OfferStatus offerStatus) { this.offerStatus = offerStatus; }
    public LocalDateTime getOfferedAt() { return offeredAt; }
    public void setOfferedAt(LocalDateTime offeredAt) { this.offeredAt = offeredAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @JsonProperty("studentName")
    public String getStudentName() {
        return student != null ? student.getFullName() : null;
    }

    @JsonProperty("studentRollNumber")
    public String getStudentRollNumber() {
        return student != null ? student.getRollNumber() : null;
    }

    @JsonProperty("companyName")
    public String getCompanyName() {
        return company != null ? company.getName() : null;
    }

    @JsonProperty("companyLogo")
    public String getCompanyLogo() {
        return company != null ? company.getLogoUrl() : null;
    }

    @JsonProperty("ctcPackage")
    public Double getCtcPackage() {
        return packageLpa;
    }

    @JsonProperty("designation")
    public String getDesignation() {
        return jobRole;
    }
}

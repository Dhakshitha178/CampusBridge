package com.campusbridge.placement.dto.response;

import com.campusbridge.placement.entity.Application;
import com.campusbridge.placement.entity.DriveEligibilityCriteria;
import com.campusbridge.placement.entity.Student;
import com.campusbridge.placement.entity.enums.ApplicationStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ApplicationStatusResponse {
    private Long id;
    private Long driveId;
    private String companyName;
    private String companyLogo;
    private String jobTitle;
    private Double packageLpa;
    private ApplicationStatus status;
    private Long currentStageId;
    private String currentStageName;
    private Integer currentStageOrder;
    private Integer totalStages;
    private LocalDateTime appliedDate;
    private LocalDateTime updatedAt;
    private String notes;

    // Student profile details for recruiter and admin portals
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private String studentRollNumber;
    private String department;
    private Double cgpa;
    private Integer backlogs;
    private String resumeUrl;
    private String resumeFileName;
    private String skills;
    private String certifications;
    private String academicYear;
    private Integer graduationYear;
    private Long collegeId;
    private String collegeName;

    // Explainable eligibility check result
    private Boolean eligible = true;
    private String ineligibilityReason = "";

    public ApplicationStatusResponse() {}

    public ApplicationStatusResponse(Application a) {
        this.id = a.getId();
        if (a.getDrive() != null) {
            this.driveId = a.getDrive().getId();
            this.jobTitle = a.getDrive().getJobTitle();
            this.packageLpa = a.getDrive().getPackageLpa();
            if (a.getDrive().getCompany() != null) {
                this.companyName = a.getDrive().getCompany().getName();
                this.companyLogo = a.getDrive().getCompany().getLogoUrl();
            }
            if (a.getDrive().getRecruitmentStages() != null) {
                this.totalStages = a.getDrive().getRecruitmentStages().size();
            }
        }
        this.status = a.getStatus();
        if (a.getCurrentStage() != null) {
            this.currentStageId = a.getCurrentStage().getId();
            this.currentStageName = a.getCurrentStage().getStageName();
            this.currentStageOrder = a.getCurrentStage().getStageOrder();
        }
        this.appliedDate = a.getAppliedDate();
        this.updatedAt = a.getUpdatedAt();
        this.notes = a.getNotes();

        if (a.getStudent() != null) {
            Student s = a.getStudent();
            this.studentId = s.getId();
            this.studentName = s.getFullName();
            this.studentEmail = s.getEmail();
            this.studentRollNumber = s.getRollNumber();
            this.department = s.getDepartment();
            this.cgpa = s.getCgpa();
            this.backlogs = s.getBacklogs();
            this.resumeUrl = s.getResumeUrl();
            this.resumeFileName = s.getResumeFileName();
            this.skills = s.getSkills();
            this.certifications = s.getCertifications();
            this.academicYear = s.getAcademicYear();
            this.graduationYear = s.getGraduationYear();
            if (s.getCollege() != null) {
                this.collegeId = s.getCollege().getId();
                this.collegeName = s.getCollege().getName();
            }

            if (a.getDrive() != null && a.getDrive().getEligibilityCriteria() != null) {
                DriveEligibilityCriteria crit = a.getDrive().getEligibilityCriteria();
                List<String> reasons = new ArrayList<>();
                if (crit.getMinCgpa() != null && s.getCgpa() != null && s.getCgpa() < crit.getMinCgpa()) {
                    reasons.add(String.format("CGPA %.2f < %.2f", s.getCgpa(), crit.getMinCgpa()));
                }
                if (crit.getMaxBacklogs() != null && s.getBacklogs() != null && s.getBacklogs() > crit.getMaxBacklogs()) {
                    reasons.add(String.format("Backlogs %d > max %d", s.getBacklogs(), crit.getMaxBacklogs()));
                }
                if (crit.getGraduationYear() != null && s.getGraduationYear() != null && !s.getGraduationYear().equals(crit.getGraduationYear())) {
                    reasons.add(String.format("Graduation batch %d != %d", s.getGraduationYear(), crit.getGraduationYear()));
                }
                if (!reasons.isEmpty()) {
                    this.eligible = false;
                    this.ineligibilityReason = String.join(", ", reasons);
                } else {
                    this.eligible = true;
                    this.ineligibilityReason = "";
                }
            }
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDriveId() { return driveId; }
    public void setDriveId(Long driveId) { this.driveId = driveId; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getCompanyLogo() { return companyLogo; }
    public void setCompanyLogo(String companyLogo) { this.companyLogo = companyLogo; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public Double getPackageLpa() { return packageLpa; }
    public void setPackageLpa(Double packageLpa) { this.packageLpa = packageLpa; }
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
    public Long getCurrentStageId() { return currentStageId; }
    public void setCurrentStageId(Long currentStageId) { this.currentStageId = currentStageId; }
    public String getCurrentStageName() { return currentStageName; }
    public void setCurrentStageName(String currentStageName) { this.currentStageName = currentStageName; }
    public Integer getCurrentStageOrder() { return currentStageOrder; }
    public void setCurrentStageOrder(Integer currentStageOrder) { this.currentStageOrder = currentStageOrder; }
    public Integer getTotalStages() { return totalStages; }
    public void setTotalStages(Integer totalStages) { this.totalStages = totalStages; }
    public LocalDateTime getAppliedDate() { return appliedDate; }
    public void setAppliedDate(LocalDateTime appliedDate) { this.appliedDate = appliedDate; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    // Student fields
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }
    public String getStudentRollNumber() { return studentRollNumber; }
    public void setStudentRollNumber(String studentRollNumber) { this.studentRollNumber = studentRollNumber; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public Double getCgpa() { return cgpa; }
    public void setCgpa(Double cgpa) { this.cgpa = cgpa; }
    public Integer getBacklogs() { return backlogs; }
    public void setBacklogs(Integer backlogs) { this.backlogs = backlogs; }
    public String getResumeUrl() { return resumeUrl; }
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }
    public String getResumeFileName() { return resumeFileName; }
    public void setResumeFileName(String resumeFileName) { this.resumeFileName = resumeFileName; }
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    public String getCertifications() { return certifications; }
    public void setCertifications(String certifications) { this.certifications = certifications; }
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    public Integer getGraduationYear() { return graduationYear; }
    public void setGraduationYear(Integer graduationYear) { this.graduationYear = graduationYear; }
    public Long getCollegeId() { return collegeId; }
    public void setCollegeId(Long collegeId) { this.collegeId = collegeId; }
    public String getCollegeName() { return collegeName; }
    public void setCollegeName(String collegeName) { this.collegeName = collegeName; }

    public Boolean getEligible() { return eligible; }
    public void setEligible(Boolean eligible) { this.eligible = eligible; }
    public String getIneligibilityReason() { return ineligibilityReason; }
    public void setIneligibilityReason(String ineligibilityReason) { this.ineligibilityReason = ineligibilityReason; }

    // Aliases for frontend flexibility
    @JsonProperty("name")
    public String getName() { return studentName; }

    @JsonProperty("rollNumber")
    public String getRollNumber() { return studentRollNumber; }
}

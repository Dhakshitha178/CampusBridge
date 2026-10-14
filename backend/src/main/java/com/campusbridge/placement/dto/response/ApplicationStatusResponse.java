package com.campusbridge.placement.dto.response;

import com.campusbridge.placement.entity.Application;
import com.campusbridge.placement.entity.enums.ApplicationStatus;

import java.time.LocalDateTime;

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
}

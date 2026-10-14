package com.campusbridge.placement.dto.response;

public class LiveDriveMonitorDto {
    private Long driveId;
    private String companyName;
    private String companyLogo;
    private String jobTitle;
    private String currentStage;
    private Integer stageOrder;
    private long candidatesInStage;
    private long totalDriveApplicants;
    private String stageStatus; // Active, Pending Evaluation, Completed
    private String pendingActions; // e.g. "5 interviews pending evaluation", "3 candidates waiting advancement"

    public LiveDriveMonitorDto() {}

    public LiveDriveMonitorDto(Long driveId, String companyName, String companyLogo, String jobTitle, String currentStage, Integer stageOrder, long candidatesInStage, long totalDriveApplicants, String stageStatus, String pendingActions) {
        this.driveId = driveId;
        this.companyName = companyName;
        this.companyLogo = companyLogo;
        this.jobTitle = jobTitle;
        this.currentStage = currentStage;
        this.stageOrder = stageOrder;
        this.candidatesInStage = candidatesInStage;
        this.totalDriveApplicants = totalDriveApplicants;
        this.stageStatus = stageStatus;
        this.pendingActions = pendingActions;
    }

    public Long getDriveId() { return driveId; }
    public void setDriveId(Long driveId) { this.driveId = driveId; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getCompanyLogo() { return companyLogo; }
    public void setCompanyLogo(String companyLogo) { this.companyLogo = companyLogo; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getCurrentStage() { return currentStage; }
    public void setCurrentStage(String currentStage) { this.currentStage = currentStage; }
    public Integer getStageOrder() { return stageOrder; }
    public void setStageOrder(Integer stageOrder) { this.stageOrder = stageOrder; }
    public long getCandidatesInStage() { return candidatesInStage; }
    public void setCandidatesInStage(long candidatesInStage) { this.candidatesInStage = candidatesInStage; }
    public long getTotalDriveApplicants() { return totalDriveApplicants; }
    public void setTotalDriveApplicants(long totalDriveApplicants) { this.totalDriveApplicants = totalDriveApplicants; }
    public String getStageStatus() { return stageStatus; }
    public void setStageStatus(String stageStatus) { this.stageStatus = stageStatus; }
    public String getPendingActions() { return pendingActions; }
    public void setPendingActions(String pendingActions) { this.pendingActions = pendingActions; }
}

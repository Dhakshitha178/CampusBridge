package com.campusbridge.placement.dto.response;

import java.util.ArrayList;
import java.util.List;

public class LiveDriveMonitorDto {
    private Long driveId;
    private String companyName;
    private String companyLogo;
    private String driveTitle;
    private String jobTitle;
    private String driveStatus = "ACTIVE";
    private Double salaryPackage;
    private Double packageLpa;
    private String workMode = "ON_SITE";
    private Integer vacancies = 0;
    private String currentActiveStage;
    private String currentStage;
    private Integer stageOrder = 1;
    private long candidatesInStage = 0;
    private long totalApplicants = 0;
    private long totalDriveApplicants = 0;
    private long inProgressCount = 0;
    private long selectedCount = 0;
    private long rejectedCount = 0;
    private String stageStatus = "Active";
    private String pendingActions;
    private List<StageBreakdownDto> stageBreakdown = new ArrayList<>();

    public static class StageBreakdownDto {
        private String stageName;
        private long candidateCount;

        public StageBreakdownDto() {}
        public StageBreakdownDto(String stageName, long candidateCount) {
            this.stageName = stageName;
            this.candidateCount = candidateCount;
        }

        public String getStageName() { return stageName; }
        public void setStageName(String stageName) { this.stageName = stageName; }
        public long getCandidateCount() { return candidateCount; }
        public void setCandidateCount(long candidateCount) { this.candidateCount = candidateCount; }
    }

    public LiveDriveMonitorDto() {}

    public LiveDriveMonitorDto(Long driveId, String companyName, String companyLogo, String jobTitle,
                               String currentStage, Integer stageOrder, long candidatesInStage,
                               long totalDriveApplicants, String stageStatus, String pendingActions) {
        this.driveId = driveId;
        this.companyName = companyName;
        this.companyLogo = companyLogo;
        this.jobTitle = jobTitle;
        this.driveTitle = jobTitle;
        this.currentStage = currentStage;
        this.currentActiveStage = currentStage;
        this.stageOrder = stageOrder;
        this.candidatesInStage = candidatesInStage;
        this.totalDriveApplicants = totalDriveApplicants;
        this.totalApplicants = totalDriveApplicants;
        this.stageStatus = stageStatus;
        this.pendingActions = pendingActions;
    }

    public Long getDriveId() { return driveId; }
    public void setDriveId(Long driveId) { this.driveId = driveId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getCompanyLogo() { return companyLogo; }
    public void setCompanyLogo(String companyLogo) { this.companyLogo = companyLogo; }

    public String getDriveTitle() { return driveTitle != null ? driveTitle : jobTitle; }
    public void setDriveTitle(String driveTitle) { this.driveTitle = driveTitle; }

    public String getJobTitle() { return jobTitle != null ? jobTitle : driveTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getDriveStatus() { return driveStatus; }
    public void setDriveStatus(String driveStatus) { this.driveStatus = driveStatus; }

    public Double getSalaryPackage() { return salaryPackage != null ? salaryPackage : packageLpa; }
    public void setSalaryPackage(Double salaryPackage) { this.salaryPackage = salaryPackage; }

    public Double getPackageLpa() { return packageLpa != null ? packageLpa : salaryPackage; }
    public void setPackageLpa(Double packageLpa) { this.packageLpa = packageLpa; }

    public String getWorkMode() { return workMode; }
    public void setWorkMode(String workMode) { this.workMode = workMode; }

    public Integer getVacancies() { return vacancies; }
    public void setVacancies(Integer vacancies) { this.vacancies = vacancies; }

    public String getCurrentActiveStage() { return currentActiveStage != null ? currentActiveStage : currentStage; }
    public void setCurrentActiveStage(String currentActiveStage) { this.currentActiveStage = currentActiveStage; }

    public String getCurrentStage() { return currentStage != null ? currentStage : currentActiveStage; }
    public void setCurrentStage(String currentStage) { this.currentStage = currentStage; }

    public Integer getStageOrder() { return stageOrder; }
    public void setStageOrder(Integer stageOrder) { this.stageOrder = stageOrder; }

    public long getCandidatesInStage() { return candidatesInStage; }
    public void setCandidatesInStage(long candidatesInStage) { this.candidatesInStage = candidatesInStage; }

    public long getTotalApplicants() { return totalApplicants > 0 ? totalApplicants : totalDriveApplicants; }
    public void setTotalApplicants(long totalApplicants) {
        this.totalApplicants = totalApplicants;
        this.totalDriveApplicants = totalApplicants;
    }

    public long getTotalDriveApplicants() { return totalDriveApplicants > 0 ? totalDriveApplicants : totalApplicants; }
    public void setTotalDriveApplicants(long totalDriveApplicants) {
        this.totalDriveApplicants = totalDriveApplicants;
        this.totalApplicants = totalDriveApplicants;
    }

    public long getInProgressCount() { return inProgressCount; }
    public void setInProgressCount(long inProgressCount) { this.inProgressCount = inProgressCount; }

    public long getSelectedCount() { return selectedCount; }
    public void setSelectedCount(long selectedCount) { this.selectedCount = selectedCount; }

    public long getRejectedCount() { return rejectedCount; }
    public void setRejectedCount(long rejectedCount) { this.rejectedCount = rejectedCount; }

    public String getStageStatus() { return stageStatus; }
    public void setStageStatus(String stageStatus) { this.stageStatus = stageStatus; }

    public String getPendingActions() { return pendingActions; }
    public void setPendingActions(String pendingActions) { this.pendingActions = pendingActions; }

    public List<StageBreakdownDto> getStageBreakdown() { return stageBreakdown; }
    public void setStageBreakdown(List<StageBreakdownDto> stageBreakdown) { this.stageBreakdown = stageBreakdown; }
}

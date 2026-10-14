package com.campusbridge.placement.dto.request;

import com.campusbridge.placement.entity.enums.EmploymentType;
import com.campusbridge.placement.entity.enums.StageType;
import com.campusbridge.placement.entity.enums.WorkMode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DriveCreateRequest {
    private String jobTitle;
    private String jobDescription;
    private Double packageLpa;
    private String location;
    private WorkMode workMode = WorkMode.ON_SITE;
    private Integer vacancies;
    private EmploymentType employmentType = EmploymentType.FULL_TIME;
    private LocalDateTime applicationDeadline;
    private LocalDate driveDate;
    private Long collegeId; // Specific college or null for all

    // Eligibility Criteria
    private String allowedDepartments;
    private Double minCgpa;
    private Integer maxBacklogs;
    private Integer graduationYear;
    private String requiredSkills;

    // Flexible Dynamic Stages
    private List<StageRequest> stages = new ArrayList<>();

    public static class StageRequest {
        private Integer stageOrder;
        private String stageName;
        private StageType stageType;
        private String description;
        private Double cutOffScore;
        private Boolean isFinalStage;

        public StageRequest() {}

        public Integer getStageOrder() { return stageOrder; }
        public void setStageOrder(Integer stageOrder) { this.stageOrder = stageOrder; }
        public String getStageName() { return stageName; }
        public void setStageName(String stageName) { this.stageName = stageName; }
        public StageType getStageType() { return stageType; }
        public void setStageType(StageType stageType) { this.stageType = stageType; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Double getCutOffScore() { return cutOffScore; }
        public void setCutOffScore(Double cutOffScore) { this.cutOffScore = cutOffScore; }
        public Boolean getIsFinalStage() { return isFinalStage; }
        public void setIsFinalStage(Boolean isFinalStage) { this.isFinalStage = isFinalStage; }
    }

    public DriveCreateRequest() {}

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
    public Long getCollegeId() { return collegeId; }
    public void setCollegeId(Long collegeId) { this.collegeId = collegeId; }
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
    public List<StageRequest> getStages() { return stages; }
    public void setStages(List<StageRequest> stages) { this.stages = stages; }
}

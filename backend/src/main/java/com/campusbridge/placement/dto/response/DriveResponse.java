package com.campusbridge.placement.dto.response;

import com.campusbridge.placement.entity.PlacementDrive;
import com.campusbridge.placement.entity.RecruitmentStage;
import com.campusbridge.placement.entity.enums.DriveStatus;
import com.campusbridge.placement.entity.enums.EmploymentType;
import com.campusbridge.placement.entity.enums.WorkMode;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DriveResponse {
    private Long id;
    private Long companyId;
    private String companyName;
    private String companyLogo;
    private String companyWebsite;
    private String companyIndustry;
    private Long collegeId;
    private String collegeName;
    private String jobTitle;
    private String jobDescription;
    private Double packageLpa;
    private String location;
    private WorkMode workMode;
    private Integer vacancies;
    private EmploymentType employmentType;
    private LocalDateTime applicationDeadline;
    private LocalDate driveDate;
    private DriveStatus status;
    private String tpoApprovalNotes;

    // Eligibility
    private String allowedDepartments;
    private Double minCgpa;
    private Integer maxBacklogs;
    private Integer graduationYear;
    private String requiredSkills;

    // Stages
    private List<StageDto> stages = new ArrayList<>();

    // Student specific eligibility evaluation
    private EligibilityResultDto studentEligibility;
    private Long totalApplications;

    public DriveResponse() {}

    public DriveResponse(PlacementDrive d) {
        this.id = d.getId();
        if (d.getCompany() != null) {
            this.companyId = d.getCompany().getId();
            this.companyName = d.getCompany().getName();
            this.companyLogo = d.getCompany().getLogoUrl();
            this.companyWebsite = d.getCompany().getWebsite();
            this.companyIndustry = d.getCompany().getIndustry();
        }
        if (d.getCollege() != null) {
            this.collegeId = d.getCollege().getId();
            this.collegeName = d.getCollege().getName();
        }
        this.jobTitle = d.getJobTitle();
        this.jobDescription = d.getJobDescription();
        this.packageLpa = d.getPackageLpa();
        this.location = d.getLocation();
        this.workMode = d.getWorkMode();
        this.vacancies = d.getVacancies();
        this.employmentType = d.getEmploymentType();
        this.applicationDeadline = d.getApplicationDeadline();
        this.driveDate = d.getDriveDate();
        this.status = d.getStatus();
        this.tpoApprovalNotes = d.getTpoApprovalNotes();

        if (d.getEligibilityCriteria() != null) {
            this.allowedDepartments = d.getEligibilityCriteria().getAllowedDepartments();
            this.minCgpa = d.getEligibilityCriteria().getMinCgpa();
            this.maxBacklogs = d.getEligibilityCriteria().getMaxBacklogs();
            this.graduationYear = d.getEligibilityCriteria().getGraduationYear();
            this.requiredSkills = d.getEligibilityCriteria().getRequiredSkills();
        }

        if (d.getRecruitmentStages() != null) {
            for (RecruitmentStage s : d.getRecruitmentStages()) {
                this.stages.add(new StageDto(s.getId(), s.getStageOrder(), s.getStageName(), s.getStageType().name(), s.getDescription(), s.getIsFinalStage()));
            }
        }
    }

    public static class StageDto {
        public Long id;
        public Integer stageOrder;
        public String stageName;
        public String stageType;
        public String description;
        public Boolean isFinalStage;

        public StageDto() {}
        public StageDto(Long id, Integer stageOrder, String stageName, String stageType, String description, Boolean isFinalStage) {
            this.id = id;
            this.stageOrder = stageOrder;
            this.stageName = stageName;
            this.stageType = stageType;
            this.description = description;
            this.isFinalStage = isFinalStage;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getCompanyLogo() { return companyLogo; }
    public void setCompanyLogo(String companyLogo) { this.companyLogo = companyLogo; }
    public String getCompanyWebsite() { return companyWebsite; }
    public void setCompanyWebsite(String companyWebsite) { this.companyWebsite = companyWebsite; }
    public String getCompanyIndustry() { return companyIndustry; }
    public void setCompanyIndustry(String companyIndustry) { this.companyIndustry = companyIndustry; }
    public Long getCollegeId() { return collegeId; }
    public void setCollegeId(Long collegeId) { this.collegeId = collegeId; }
    public String getCollegeName() { return collegeName; }
    public void setCollegeName(String collegeName) { this.collegeName = collegeName; }
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
    public List<StageDto> getStages() { return stages; }
    public void setStages(List<StageDto> stages) { this.stages = stages; }
    public EligibilityResultDto getStudentEligibility() { return studentEligibility; }
    public void setStudentEligibility(EligibilityResultDto studentEligibility) { this.studentEligibility = studentEligibility; }
    public Long getTotalApplications() { return totalApplications; }
    public void setTotalApplications(Long totalApplications) { this.totalApplications = totalApplications; }

    // Frontend-friendly aliases
    @JsonProperty("title")
    public String getTitle() { return jobTitle; }

    @JsonProperty("description")
    public String getDescription() { return jobDescription; }

    @JsonProperty("salaryPackage")
    public Double getSalaryPackage() { return packageLpa; }

    @JsonProperty("jobRole")
    public String getJobRole() { return jobTitle; }

    // Nested criteria object for frontend consumption
    @JsonProperty("criteria")
    public CriteriaDto getCriteria() {
        return new CriteriaDto(minCgpa, maxBacklogs, graduationYear, allowedDepartments, requiredSkills);
    }

    public static class CriteriaDto {
        public Double minCgpa;
        public Integer maxBacklogs;
        public Integer graduationYear;
        public String allowedDepartments;
        public String requiredSkills;

        public CriteriaDto() {}
        public CriteriaDto(Double minCgpa, Integer maxBacklogs, Integer graduationYear, String allowedDepartments, String requiredSkills) {
            this.minCgpa = minCgpa;
            this.maxBacklogs = maxBacklogs;
            this.graduationYear = graduationYear;
            this.allowedDepartments = allowedDepartments;
            this.requiredSkills = requiredSkills;
        }
    }
}

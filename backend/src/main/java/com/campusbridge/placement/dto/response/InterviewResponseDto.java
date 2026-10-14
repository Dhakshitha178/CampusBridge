package com.campusbridge.placement.dto.response;

import com.campusbridge.placement.entity.Interview;
import com.campusbridge.placement.entity.enums.InterviewStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public class InterviewResponseDto {
    private Long id;
    private Long driveId;
    private String companyName;
    private String companyLogo;
    private String jobTitle;
    private Long stageId;
    private String stageName;
    private String stageType;
    private Long studentId;
    private String studentName;
    private String studentRollNumber;
    private String studentDepartment;
    private LocalDate interviewDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String mode;
    private String meetingLink;
    private String venue;
    private Long panelId;
    private String panelName;
    private InterviewStatus status;
    private String instructions;
    private String requiredDocuments;
    private boolean hasConflict;
    private String conflictReason;

    public InterviewResponseDto() {}

    public InterviewResponseDto(Interview i) {
        this.id = i.getId();
        if (i.getDrive() != null) {
            this.driveId = i.getDrive().getId();
            this.jobTitle = i.getDrive().getJobTitle();
            if (i.getDrive().getCompany() != null) {
                this.companyName = i.getDrive().getCompany().getName();
                this.companyLogo = i.getDrive().getCompany().getLogoUrl();
            }
        }
        if (i.getStage() != null) {
            this.stageId = i.getStage().getId();
            this.stageName = i.getStage().getStageName();
            this.stageType = i.getStage().getStageType().name();
        }
        if (i.getStudent() != null) {
            this.studentId = i.getStudent().getId();
            this.studentName = i.getStudent().getFullName();
            this.studentRollNumber = i.getStudent().getRollNumber();
            this.studentDepartment = i.getStudent().getDepartment();
        }
        this.interviewDate = i.getInterviewDate();
        this.startTime = i.getStartTime();
        this.endTime = i.getEndTime();
        this.mode = i.getMode();
        this.meetingLink = i.getMeetingLink();
        this.venue = i.getVenue();
        if (i.getPanel() != null) {
            this.panelId = i.getPanel().getId();
            this.panelName = i.getPanel().getPanelName();
        }
        this.status = i.getStatus();
        this.instructions = i.getInstructions();
        this.requiredDocuments = i.getRequiredDocuments();
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
    public Long getStageId() { return stageId; }
    public void setStageId(Long stageId) { this.stageId = stageId; }
    public String getStageName() { return stageName; }
    public void setStageName(String stageName) { this.stageName = stageName; }
    public String getStageType() { return stageType; }
    public void setStageType(String stageType) { this.stageType = stageType; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getStudentRollNumber() { return studentRollNumber; }
    public void setStudentRollNumber(String studentRollNumber) { this.studentRollNumber = studentRollNumber; }
    public String getStudentDepartment() { return studentDepartment; }
    public void setStudentDepartment(String studentDepartment) { this.studentDepartment = studentDepartment; }
    public LocalDate getInterviewDate() { return interviewDate; }
    public void setInterviewDate(LocalDate interviewDate) { this.interviewDate = interviewDate; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    public String getMeetingLink() { return meetingLink; }
    public void setMeetingLink(String meetingLink) { this.meetingLink = meetingLink; }
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    public Long getPanelId() { return panelId; }
    public void setPanelId(Long panelId) { this.panelId = panelId; }
    public String getPanelName() { return panelName; }
    public void setPanelName(String panelName) { this.panelName = panelName; }
    public InterviewStatus getStatus() { return status; }
    public void setStatus(InterviewStatus status) { this.status = status; }
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    public String getRequiredDocuments() { return requiredDocuments; }
    public void setRequiredDocuments(String requiredDocuments) { this.requiredDocuments = requiredDocuments; }
    public boolean isHasConflict() { return hasConflict; }
    public void setHasConflict(boolean hasConflict) { this.hasConflict = hasConflict; }
    public String getConflictReason() { return conflictReason; }
    public void setConflictReason(String conflictReason) { this.conflictReason = conflictReason; }
}

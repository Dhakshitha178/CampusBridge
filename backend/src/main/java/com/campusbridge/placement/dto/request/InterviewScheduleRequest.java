package com.campusbridge.placement.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class InterviewScheduleRequest {
    private Long driveId;
    private Long stageId;
    private List<Long> studentIds;
    private LocalDate interviewDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String mode = "ONLINE"; // ONLINE or OFFLINE
    private String meetingLink;
    private String venue;
    private Long panelId;
    private String instructions;
    private String requiredDocuments;

    public InterviewScheduleRequest() {}

    public Long getDriveId() { return driveId; }
    public void setDriveId(Long driveId) { this.driveId = driveId; }
    public Long getStageId() { return stageId; }
    public void setStageId(Long stageId) { this.stageId = stageId; }
    public List<Long> getStudentIds() { return studentIds; }
    public void setStudentIds(List<Long> studentIds) { this.studentIds = studentIds; }
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
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    public String getRequiredDocuments() { return requiredDocuments; }
    public void setRequiredDocuments(String requiredDocuments) { this.requiredDocuments = requiredDocuments; }
}

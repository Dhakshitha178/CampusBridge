package com.campusbridge.placement.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

public class ConflictDetailDto {
    private String conflictType; // STUDENT_CONFLICT, INTERVIEWER_CONFLICT, VENUE_CONFLICT
    private String affectedResource; // Student Name / Interviewer Name / Venue Name
    private Long existingInterviewId;
    private String existingCompanyName;
    private String existingRoundName;
    private LocalDate interviewDate;
    private LocalTime existingStartTime;
    private LocalTime existingEndTime;
    private LocalTime requestedStartTime;
    private LocalTime requestedEndTime;
    private String suggestedResolution;

    public ConflictDetailDto() {}

    public ConflictDetailDto(String conflictType, String affectedResource, Long existingInterviewId, String existingCompanyName, String existingRoundName, LocalDate interviewDate, LocalTime existingStartTime, LocalTime existingEndTime, LocalTime requestedStartTime, LocalTime requestedEndTime, String suggestedResolution) {
        this.conflictType = conflictType;
        this.affectedResource = affectedResource;
        this.existingInterviewId = existingInterviewId;
        this.existingCompanyName = existingCompanyName;
        this.existingRoundName = existingRoundName;
        this.interviewDate = interviewDate;
        this.existingStartTime = existingStartTime;
        this.existingEndTime = existingEndTime;
        this.requestedStartTime = requestedStartTime;
        this.requestedEndTime = requestedEndTime;
        this.suggestedResolution = suggestedResolution;
    }

    public String getConflictType() { return conflictType; }
    public void setConflictType(String conflictType) { this.conflictType = conflictType; }
    public String getAffectedResource() { return affectedResource; }
    public void setAffectedResource(String affectedResource) { this.affectedResource = affectedResource; }
    public Long getExistingInterviewId() { return existingInterviewId; }
    public void setExistingInterviewId(Long existingInterviewId) { this.existingInterviewId = existingInterviewId; }
    public String getExistingCompanyName() { return existingCompanyName; }
    public void setExistingCompanyName(String existingCompanyName) { this.existingCompanyName = existingCompanyName; }
    public String getExistingRoundName() { return existingRoundName; }
    public void setExistingRoundName(String existingRoundName) { this.existingRoundName = existingRoundName; }
    public LocalDate getInterviewDate() { return interviewDate; }
    public void setInterviewDate(LocalDate interviewDate) { this.interviewDate = interviewDate; }
    public LocalTime getExistingStartTime() { return existingStartTime; }
    public void setExistingStartTime(LocalTime existingStartTime) { this.existingStartTime = existingStartTime; }
    public LocalTime getExistingEndTime() { return existingEndTime; }
    public void setExistingEndTime(LocalTime existingEndTime) { this.existingEndTime = existingEndTime; }
    public LocalTime getRequestedStartTime() { return requestedStartTime; }
    public void setRequestedStartTime(LocalTime requestedStartTime) { this.requestedStartTime = requestedStartTime; }
    public LocalTime getRequestedEndTime() { return requestedEndTime; }
    public void setRequestedEndTime(LocalTime requestedEndTime) { this.requestedEndTime = requestedEndTime; }
    public String getSuggestedResolution() { return suggestedResolution; }
    public void setSuggestedResolution(String suggestedResolution) { this.suggestedResolution = suggestedResolution; }
}

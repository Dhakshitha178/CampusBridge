package com.campusbridge.placement.dto.request;

public class ApplicationRequest {
    private Long driveId;
    private String notes;

    public ApplicationRequest() {}

    public Long getDriveId() { return driveId; }
    public void setDriveId(Long driveId) { this.driveId = driveId; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

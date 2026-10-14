package com.campusbridge.placement.dto.request;

public class DriveApprovalRequest {
    private boolean approved;
    private String notes;

    public DriveApprovalRequest() {}

    public DriveApprovalRequest(boolean approved, String notes) {
        this.approved = approved;
        this.notes = notes;
    }

    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

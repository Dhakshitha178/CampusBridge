package com.campusbridge.placement.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

public class RescheduleReviewRequest {
    private boolean approved;
    private String tpoRemarks;
    private LocalDate newDate;
    private LocalTime newStartTime;
    private LocalTime newEndTime;

    public RescheduleReviewRequest() {}

    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }
    public String getTpoRemarks() { return tpoRemarks; }
    public void setTpoRemarks(String tpoRemarks) { this.tpoRemarks = tpoRemarks; }
    public LocalDate getNewDate() { return newDate; }
    public void setNewDate(LocalDate newDate) { this.newDate = newDate; }
    public LocalTime getNewStartTime() { return newStartTime; }
    public void setNewStartTime(LocalTime newStartTime) { this.newStartTime = newStartTime; }
    public LocalTime getNewEndTime() { return newEndTime; }
    public void setNewEndTime(LocalTime newEndTime) { this.newEndTime = newEndTime; }
}

package com.campusbridge.placement.dto.request;

public class RescheduleSubmitRequest {
    private Long interviewId;
    private String reason;
    private String preferredAlternativeTime;
    private String preferredDate;
    private String preferredTimeSlot;

    public RescheduleSubmitRequest() {}

    public RescheduleSubmitRequest(String reason, String preferredAlternativeTime) {
        this.reason = reason;
        this.preferredAlternativeTime = preferredAlternativeTime;
    }

    public Long getInterviewId() { return interviewId; }
    public void setInterviewId(Long interviewId) { this.interviewId = interviewId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getPreferredAlternativeTime() {
        if (preferredAlternativeTime != null && !preferredAlternativeTime.isBlank()) {
            return preferredAlternativeTime;
        }
        if (preferredDate != null && preferredTimeSlot != null) {
            return preferredDate + " " + preferredTimeSlot;
        }
        return preferredDate != null ? preferredDate : preferredTimeSlot;
    }
    public void setPreferredAlternativeTime(String preferredAlternativeTime) { this.preferredAlternativeTime = preferredAlternativeTime; }

    public String getPreferredDate() { return preferredDate; }
    public void setPreferredDate(String preferredDate) { this.preferredDate = preferredDate; }

    public String getPreferredTimeSlot() { return preferredTimeSlot; }
    public void setPreferredTimeSlot(String preferredTimeSlot) { this.preferredTimeSlot = preferredTimeSlot; }
}

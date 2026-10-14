package com.campusbridge.placement.dto.request;

public class RescheduleSubmitRequest {
    private String reason;
    private String preferredAlternativeTime;

    public RescheduleSubmitRequest() {}

    public RescheduleSubmitRequest(String reason, String preferredAlternativeTime) {
        this.reason = reason;
        this.preferredAlternativeTime = preferredAlternativeTime;
    }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getPreferredAlternativeTime() { return preferredAlternativeTime; }
    public void setPreferredAlternativeTime(String preferredAlternativeTime) { this.preferredAlternativeTime = preferredAlternativeTime; }
}

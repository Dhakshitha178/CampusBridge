package com.campusbridge.placement.dto.response;

public class StudentDashboardStatsDto {
    private long totalApplications;
    private long shortlistedCount;
    private long activeProcesses;
    private long upcomingInterviews;
    private long completedInterviews;
    private long offersReceived;
    private long detectedConflicts;
    private long pendingRescheduleRequests;
    private long unreadNotifications;
    private Double profileCgpa;
    private String placementStatus;

    public StudentDashboardStatsDto() {}

    public long getTotalApplications() { return totalApplications; }
    public void setTotalApplications(long totalApplications) { this.totalApplications = totalApplications; }
    public long getShortlistedCount() { return shortlistedCount; }
    public void setShortlistedCount(long shortlistedCount) { this.shortlistedCount = shortlistedCount; }
    public long getActiveProcesses() { return activeProcesses; }
    public void setActiveProcesses(long activeProcesses) { this.activeProcesses = activeProcesses; }
    public long getUpcomingInterviews() { return upcomingInterviews; }
    public void setUpcomingInterviews(long upcomingInterviews) { this.upcomingInterviews = upcomingInterviews; }
    public long getCompletedInterviews() { return completedInterviews; }
    public void setCompletedInterviews(long completedInterviews) { this.completedInterviews = completedInterviews; }
    public long getOffersReceived() { return offersReceived; }
    public void setOffersReceived(long offersReceived) { this.offersReceived = offersReceived; }
    public long getDetectedConflicts() { return detectedConflicts; }
    public void setDetectedConflicts(long detectedConflicts) { this.detectedConflicts = detectedConflicts; }
    public long getPendingRescheduleRequests() { return pendingRescheduleRequests; }
    public void setPendingRescheduleRequests(long pendingRescheduleRequests) { this.pendingRescheduleRequests = pendingRescheduleRequests; }
    public long getUnreadNotifications() { return unreadNotifications; }
    public void setUnreadNotifications(long unreadNotifications) { this.unreadNotifications = unreadNotifications; }
    public Double getProfileCgpa() { return profileCgpa; }
    public void setProfileCgpa(Double profileCgpa) { this.profileCgpa = profileCgpa; }
    public String getPlacementStatus() { return placementStatus; }
    public void setPlacementStatus(String placementStatus) { this.placementStatus = placementStatus; }
}

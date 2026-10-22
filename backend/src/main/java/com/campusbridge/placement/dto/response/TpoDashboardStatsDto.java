package com.campusbridge.placement.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TpoDashboardStatsDto {
    private long totalStudents;
    private long placedStudents;
    private long activeCompanies;
    private long activeDrives;
    private long pendingApprovalDrives;
    private long totalApplications;
    private long shortlistedCount;
    private long interviewsToday;
    private long totalOffers;
    private long detectedConflicts;
    private long pendingRescheduleRequests;
    private double placementPercentage;
    private double avgPackage;
    private double highestPackage;

    public TpoDashboardStatsDto() {}

    public long getTotalStudents() { return totalStudents; }
    public void setTotalStudents(long totalStudents) { this.totalStudents = totalStudents; }
    public long getPlacedStudents() { return placedStudents; }
    public void setPlacedStudents(long placedStudents) { this.placedStudents = placedStudents; }
    public long getActiveCompanies() { return activeCompanies; }
    public void setActiveCompanies(long activeCompanies) { this.activeCompanies = activeCompanies; }
    public long getActiveDrives() { return activeDrives; }
    public void setActiveDrives(long activeDrives) { this.activeDrives = activeDrives; }
    public long getPendingApprovalDrives() { return pendingApprovalDrives; }
    public void setPendingApprovalDrives(long pendingApprovalDrives) { this.pendingApprovalDrives = pendingApprovalDrives; }
    public long getTotalApplications() { return totalApplications; }
    public void setTotalApplications(long totalApplications) { this.totalApplications = totalApplications; }
    public long getShortlistedCount() { return shortlistedCount; }
    public void setShortlistedCount(long shortlistedCount) { this.shortlistedCount = shortlistedCount; }
    public long getInterviewsToday() { return interviewsToday; }
    public void setInterviewsToday(long interviewsToday) { this.interviewsToday = interviewsToday; }
    public long getTotalOffers() { return totalOffers; }
    public void setTotalOffers(long totalOffers) { this.totalOffers = totalOffers; }
    public long getDetectedConflicts() { return detectedConflicts; }
    public void setDetectedConflicts(long detectedConflicts) { this.detectedConflicts = detectedConflicts; }
    public long getPendingRescheduleRequests() { return pendingRescheduleRequests; }
    public void setPendingRescheduleRequests(long pendingRescheduleRequests) { this.pendingRescheduleRequests = pendingRescheduleRequests; }
    public double getPlacementPercentage() { return placementPercentage; }
    public void setPlacementPercentage(double placementPercentage) { this.placementPercentage = placementPercentage; }
    public double getAvgPackage() { return avgPackage; }
    public void setAvgPackage(double avgPackage) { this.avgPackage = avgPackage; }
    public double getHighestPackage() { return highestPackage; }
    public void setHighestPackage(double highestPackage) { this.highestPackage = highestPackage; }
}

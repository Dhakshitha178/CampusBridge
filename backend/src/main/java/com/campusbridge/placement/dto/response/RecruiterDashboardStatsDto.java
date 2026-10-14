package com.campusbridge.placement.dto.response;

public class RecruiterDashboardStatsDto {
    private String companyName;
    private long totalDrives;
    private long activeDrives;
    private long totalApplicants;
    private long shortlistedCandidates;
    private long scheduledInterviews;
    private long completedInterviews;
    private long totalOffers;
    private double overallSelectionRate;

    public RecruiterDashboardStatsDto() {}

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public long getTotalDrives() { return totalDrives; }
    public void setTotalDrives(long totalDrives) { this.totalDrives = totalDrives; }
    public long getActiveDrives() { return activeDrives; }
    public void setActiveDrives(long activeDrives) { this.activeDrives = activeDrives; }
    public long getTotalApplicants() { return totalApplicants; }
    public void setTotalApplicants(long totalApplicants) { this.totalApplicants = totalApplicants; }
    public long getShortlistedCandidates() { return shortlistedCandidates; }
    public void setShortlistedCandidates(long shortlistedCandidates) { this.shortlistedCandidates = shortlistedCandidates; }
    public long getScheduledInterviews() { return scheduledInterviews; }
    public void setScheduledInterviews(long scheduledInterviews) { this.scheduledInterviews = scheduledInterviews; }
    public long getCompletedInterviews() { return completedInterviews; }
    public void setCompletedInterviews(long completedInterviews) { this.completedInterviews = completedInterviews; }
    public long getTotalOffers() { return totalOffers; }
    public void setTotalOffers(long totalOffers) { this.totalOffers = totalOffers; }
    public double getOverallSelectionRate() { return overallSelectionRate; }
    public void setOverallSelectionRate(double overallSelectionRate) { this.overallSelectionRate = overallSelectionRate; }
}

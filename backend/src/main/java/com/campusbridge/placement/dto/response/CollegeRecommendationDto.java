package com.campusbridge.placement.dto.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollegeRecommendationDto {
    private Long collegeId;
    private String collegeName;
    private String state;
    private double priorityScore; // 0 to 100
    private String priorityTier; // HIGH PRIORITY, MEDIUM PRIORITY, LOW PRIORITY, INSUFFICIENT DATA
    private boolean sufficientData;
    private int historicalDrivesCount;
    private int totalHistoricalApplicants;
    private Map<String, Double> factorScores = new HashMap<>(); // Factor -> Normalized score 0-100
    private List<String> positiveEvidence = new ArrayList<>();
    private List<String> negativeEvidence = new ArrayList<>();
    private String explanation;
    private String dataLimitations;

    public CollegeRecommendationDto() {}

    public Long getCollegeId() { return collegeId; }
    public void setCollegeId(Long collegeId) { this.collegeId = collegeId; }
    public String getCollegeName() { return collegeName; }
    public void setCollegeName(String collegeName) { this.collegeName = collegeName; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public double getPriorityScore() { return priorityScore; }
    public void setPriorityScore(double priorityScore) { this.priorityScore = priorityScore; }
    public String getPriorityTier() { return priorityTier; }
    public void setPriorityTier(String priorityTier) { this.priorityTier = priorityTier; }
    public boolean isSufficientData() { return sufficientData; }
    public void setSufficientData(boolean sufficientData) { this.sufficientData = sufficientData; }
    public int getHistoricalDrivesCount() { return historicalDrivesCount; }
    public void setHistoricalDrivesCount(int historicalDrivesCount) { this.historicalDrivesCount = historicalDrivesCount; }
    public int getTotalHistoricalApplicants() { return totalHistoricalApplicants; }
    public void setTotalHistoricalApplicants(int totalHistoricalApplicants) { this.totalHistoricalApplicants = totalHistoricalApplicants; }
    public Map<String, Double> getFactorScores() { return factorScores; }
    public void setFactorScores(Map<String, Double> factorScores) { this.factorScores = factorScores; }
    public List<String> getPositiveEvidence() { return positiveEvidence; }
    public void setPositiveEvidence(List<String> positiveEvidence) { this.positiveEvidence = positiveEvidence; }
    public List<String> getNegativeEvidence() { return negativeEvidence; }
    public void setNegativeEvidence(List<String> negativeEvidence) { this.negativeEvidence = negativeEvidence; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public String getDataLimitations() { return dataLimitations; }
    public void setDataLimitations(String dataLimitations) { this.dataLimitations = dataLimitations; }
}

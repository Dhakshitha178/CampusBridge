package com.campusbridge.placement.dto.request;

import com.campusbridge.placement.entity.enums.EvaluationDecision;

public class EvaluationRequest {
    private Double technicalScore;
    private Double communicationScore;
    private Double problemSolvingScore;
    private Double overallRating;
    private String comments;
    private EvaluationDecision decision; // PASS, FAIL, HOLD

    public EvaluationRequest() {}

    public Double getTechnicalScore() { return technicalScore; }
    public void setTechnicalScore(Double technicalScore) { this.technicalScore = technicalScore; }
    public Double getCommunicationScore() { return communicationScore; }
    public void setCommunicationScore(Double communicationScore) { this.communicationScore = communicationScore; }
    public Double getProblemSolvingScore() { return problemSolvingScore; }
    public void setProblemSolvingScore(Double problemSolvingScore) { this.problemSolvingScore = problemSolvingScore; }
    public Double getOverallRating() { return overallRating; }
    public void setOverallRating(Double overallRating) { this.overallRating = overallRating; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    public EvaluationDecision getDecision() { return decision; }
    public void setDecision(EvaluationDecision decision) { this.decision = decision; }
}

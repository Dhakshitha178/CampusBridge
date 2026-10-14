package com.campusbridge.placement.dto.request;

import com.campusbridge.placement.entity.enums.EvaluationDecision;

public class BulkResultRowDto {
    private String rollNumber;
    private String stageName;
    private Double score;
    private EvaluationDecision decision;
    private String comments;

    public BulkResultRowDto() {}

    public BulkResultRowDto(String rollNumber, String stageName, Double score, EvaluationDecision decision, String comments) {
        this.rollNumber = rollNumber;
        this.stageName = stageName;
        this.score = score;
        this.decision = decision;
        this.comments = comments;
    }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }
    public String getStageName() { return stageName; }
    public void setStageName(String stageName) { this.stageName = stageName; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public EvaluationDecision getDecision() { return decision; }
    public void setDecision(EvaluationDecision decision) { this.decision = decision; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}

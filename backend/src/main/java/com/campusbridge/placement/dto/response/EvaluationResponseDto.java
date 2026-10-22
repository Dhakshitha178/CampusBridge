package com.campusbridge.placement.dto.response;

import com.campusbridge.placement.entity.InterviewEvaluation;
import com.campusbridge.placement.entity.enums.EvaluationDecision;

import java.time.LocalDateTime;

/**
 * Safe DTO for returning interview evaluation data without exposing Hibernate lazy proxies.
 */
public class EvaluationResponseDto {

    private Long id;
    private Long interviewId;
    private Long applicationId;
    private Double technicalScore;
    private Double communicationScore;
    private Double problemSolvingScore;
    private Double overallRating;
    // Expose as both "comments" and "feedback" so frontend works regardless of field name
    private String comments;
    private EvaluationDecision decision;
    private String evaluatedBy;
    private LocalDateTime evaluatedAt;

    public EvaluationResponseDto() {}

    public EvaluationResponseDto(InterviewEvaluation eval) {
        this.id = eval.getId();
        if (eval.getInterview() != null) {
            this.interviewId = eval.getInterview().getId();
        }
        if (eval.getApplication() != null) {
            this.applicationId = eval.getApplication().getId();
        }
        this.technicalScore = eval.getTechnicalScore();
        this.communicationScore = eval.getCommunicationScore();
        this.problemSolvingScore = eval.getProblemSolvingScore();
        this.overallRating = eval.getOverallRating();
        this.comments = eval.getComments();
        this.decision = eval.getDecision();
        this.evaluatedBy = eval.getEvaluatedBy();
        this.evaluatedAt = eval.getEvaluatedAt();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getInterviewId() { return interviewId; }
    public void setInterviewId(Long interviewId) { this.interviewId = interviewId; }
    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }
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
    // Alias so frontend reading `feedback` also works
    public String getFeedback() { return comments; }
    public EvaluationDecision getDecision() { return decision; }
    public void setDecision(EvaluationDecision decision) { this.decision = decision; }
    public String getEvaluatedBy() { return evaluatedBy; }
    public void setEvaluatedBy(String evaluatedBy) { this.evaluatedBy = evaluatedBy; }
    public LocalDateTime getEvaluatedAt() { return evaluatedAt; }
    public void setEvaluatedAt(LocalDateTime evaluatedAt) { this.evaluatedAt = evaluatedAt; }
}

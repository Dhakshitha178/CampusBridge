package com.campusbridge.placement.entity;

import com.campusbridge.placement.entity.enums.EvaluationDecision;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "interview_evaluations")
public class InterviewEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Column(name = "technical_score")
    private Double technicalScore;

    @Column(name = "communication_score")
    private Double communicationScore;

    @Column(name = "problem_solving_score")
    private Double problemSolvingScore;

    @Column(name = "overall_rating")
    private Double overallRating;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EvaluationDecision decision;

    @Column(name = "evaluated_by")
    private String evaluatedBy;

    @Column(name = "evaluated_at")
    private LocalDateTime evaluatedAt = LocalDateTime.now();

    public InterviewEvaluation() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Interview getInterview() { return interview; }
    public void setInterview(Interview interview) { this.interview = interview; }
    public Application getApplication() { return application; }
    public void setApplication(Application application) { this.application = application; }
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
    public String getEvaluatedBy() { return evaluatedBy; }
    public void setEvaluatedBy(String evaluatedBy) { this.evaluatedBy = evaluatedBy; }
    public LocalDateTime getEvaluatedAt() { return evaluatedAt; }
    public void setEvaluatedAt(LocalDateTime evaluatedAt) { this.evaluatedAt = evaluatedAt; }
}

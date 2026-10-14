package com.campusbridge.placement.entity;

import com.campusbridge.placement.entity.enums.RescheduleStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "reschedule_requests")
public class RescheduleRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String reason;

    @Column(name = "preferred_alternative_time")
    private String preferredAlternativeTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RescheduleStatus status = RescheduleStatus.PENDING;

    @Column(name = "tpo_remarks", columnDefinition = "TEXT")
    private String tpoRemarks;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "proposed_new_date")
    private LocalDate proposedNewDate;

    @JsonFormat(pattern = "HH:mm:ss")
    @Column(name = "proposed_new_start_time")
    private LocalTime proposedNewStartTime;

    @JsonFormat(pattern = "HH:mm:ss")
    @Column(name = "proposed_new_end_time")
    private LocalTime proposedNewEndTime;

    @Column(name = "reviewed_by")
    private String reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public RescheduleRequest() {}

    public RescheduleRequest(Interview interview, Student student, String reason, String preferredAlternativeTime) {
        this.interview = interview;
        this.student = student;
        this.reason = reason;
        this.preferredAlternativeTime = preferredAlternativeTime;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Interview getInterview() { return interview; }
    public void setInterview(Interview interview) { this.interview = interview; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getPreferredAlternativeTime() { return preferredAlternativeTime; }
    public void setPreferredAlternativeTime(String preferredAlternativeTime) { this.preferredAlternativeTime = preferredAlternativeTime; }
    public RescheduleStatus getStatus() { return status; }
    public void setStatus(RescheduleStatus status) { this.status = status; }
    public String getTpoRemarks() { return tpoRemarks; }
    public void setTpoRemarks(String tpoRemarks) { this.tpoRemarks = tpoRemarks; }
    public LocalDate getProposedNewDate() { return proposedNewDate; }
    public void setProposedNewDate(LocalDate proposedNewDate) { this.proposedNewDate = proposedNewDate; }
    public LocalTime getProposedNewStartTime() { return proposedNewStartTime; }
    public void setProposedNewStartTime(LocalTime proposedNewStartTime) { this.proposedNewStartTime = proposedNewStartTime; }
    public LocalTime getProposedNewEndTime() { return proposedNewEndTime; }
    public void setProposedNewEndTime(LocalTime proposedNewEndTime) { this.proposedNewEndTime = proposedNewEndTime; }
    public String getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

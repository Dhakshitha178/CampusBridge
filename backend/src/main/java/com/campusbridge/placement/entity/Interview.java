package com.campusbridge.placement.entity;

import com.campusbridge.placement.entity.enums.InterviewStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "interviews")
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "drive_id", nullable = false)
    private PlacementDrive drive;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "stage_id", nullable = false)
    private RecruitmentStage stage;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "interview_date", nullable = false)
    private LocalDate interviewDate;

    @JsonFormat(pattern = "HH:mm:ss")
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm:ss")
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private String mode = "ONLINE"; // ONLINE or OFFLINE

    @Column(name = "meeting_link", length = 500)
    private String meetingLink;

    private String venue;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "panel_id")
    private InterviewPanel panel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterviewStatus status = InterviewStatus.SCHEDULED;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    @Column(name = "required_documents", columnDefinition = "TEXT")
    private String requiredDocuments;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Interview() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public PlacementDrive getDrive() { return drive; }
    public void setDrive(PlacementDrive drive) { this.drive = drive; }
    public RecruitmentStage getStage() { return stage; }
    public void setStage(RecruitmentStage stage) { this.stage = stage; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public LocalDate getInterviewDate() { return interviewDate; }
    public void setInterviewDate(LocalDate interviewDate) { this.interviewDate = interviewDate; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    public String getMeetingLink() { return meetingLink; }
    public void setMeetingLink(String meetingLink) { this.meetingLink = meetingLink; }
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    public InterviewPanel getPanel() { return panel; }
    public void setPanel(InterviewPanel panel) { this.panel = panel; }
    public InterviewStatus getStatus() { return status; }
    public void setStatus(InterviewStatus status) { this.status = status; }
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    public String getRequiredDocuments() { return requiredDocuments; }
    public void setRequiredDocuments(String requiredDocuments) { this.requiredDocuments = requiredDocuments; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

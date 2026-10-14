package com.campusbridge.placement.entity;

import com.campusbridge.placement.entity.enums.ApplicationStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"drive_id", "student_id"})
})
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "drive_id", nullable = false)
    private PlacementDrive drive;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "current_stage_id")
    private RecruitmentStage currentStage;

    @Column(name = "applied_date")
    private LocalDateTime appliedDate = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String notes;

    public Application() {}

    public Application(PlacementDrive drive, Student student, ApplicationStatus status, RecruitmentStage currentStage) {
        this.drive = drive;
        this.student = student;
        this.status = status;
        this.currentStage = currentStage;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public PlacementDrive getDrive() { return drive; }
    public void setDrive(PlacementDrive drive) { this.drive = drive; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
    public RecruitmentStage getCurrentStage() { return currentStage; }
    public void setCurrentStage(RecruitmentStage currentStage) { this.currentStage = currentStage; }
    public LocalDateTime getAppliedDate() { return appliedDate; }
    public void setAppliedDate(LocalDateTime appliedDate) { this.appliedDate = appliedDate; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

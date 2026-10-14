package com.campusbridge.placement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "application_status_history")
public class ApplicationStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Column(nullable = false)
    private String status;

    @Column(name = "stage_name")
    private String stageName;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "changed_at")
    private LocalDateTime changedAt = LocalDateTime.now();

    @Column(name = "changed_by")
    private String changedBy;

    public ApplicationStatusHistory() {}

    public ApplicationStatusHistory(Application application, String status, String stageName, String remarks, String changedBy) {
        this.application = application;
        this.status = status;
        this.stageName = stageName;
        this.remarks = remarks;
        this.changedBy = changedBy;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Application getApplication() { return application; }
    public void setApplication(Application application) { this.application = application; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getStageName() { return stageName; }
    public void setStageName(String stageName) { this.stageName = stageName; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }
    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }
}

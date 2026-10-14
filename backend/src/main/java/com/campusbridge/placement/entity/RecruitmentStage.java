package com.campusbridge.placement.entity;

import com.campusbridge.placement.entity.enums.StageType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recruitment_stages")
public class RecruitmentStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drive_id", nullable = false)
    @JsonBackReference
    private PlacementDrive drive;

    @Column(name = "stage_order", nullable = false)
    private Integer stageOrder;

    @Column(name = "stage_name", nullable = false)
    private String stageName;

    @Enumerated(EnumType.STRING)
    @Column(name = "stage_type", nullable = false)
    private StageType stageType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "cut_off_score")
    private Double cutOffScore;

    @Column(name = "is_final_stage")
    private Boolean isFinalStage = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public RecruitmentStage() {}

    public RecruitmentStage(PlacementDrive drive, Integer stageOrder, String stageName, StageType stageType, String description, Boolean isFinalStage) {
        this.drive = drive;
        this.stageOrder = stageOrder;
        this.stageName = stageName;
        this.stageType = stageType;
        this.description = description;
        this.isFinalStage = isFinalStage;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public PlacementDrive getDrive() { return drive; }
    public void setDrive(PlacementDrive drive) { this.drive = drive; }
    public Integer getStageOrder() { return stageOrder; }
    public void setStageOrder(Integer stageOrder) { this.stageOrder = stageOrder; }
    public String getStageName() { return stageName; }
    public void setStageName(String stageName) { this.stageName = stageName; }
    public StageType getStageType() { return stageType; }
    public void setStageType(StageType stageType) { this.stageType = stageType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getCutOffScore() { return cutOffScore; }
    public void setCutOffScore(Double cutOffScore) { this.cutOffScore = cutOffScore; }
    public Boolean getIsFinalStage() { return isFinalStage; }
    public void setIsFinalStage(Boolean isFinalStage) { this.isFinalStage = isFinalStage; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

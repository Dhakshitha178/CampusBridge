package com.campusbridge.placement.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "weekly_placement_reports")
public class WeeklyPlacementReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "college_id", nullable = false)
    private College college;

    @Column(name = "report_title", nullable = false)
    private String reportTitle;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "reporting_period_start", nullable = false)
    private LocalDate reportingPeriodStart;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "reporting_period_end", nullable = false)
    private LocalDate reportingPeriodEnd;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt = LocalDateTime.now();

    @Column(name = "snapshot_json", columnDefinition = "LONGTEXT")
    private String snapshotJson;

    @Column(name = "total_drives")
    private Integer totalDrives = 0;

    @Column(name = "total_applications")
    private Integer totalApplications = 0;

    @Column(name = "total_shortlisted")
    private Integer totalShortlisted = 0;

    @Column(name = "total_interviews")
    private Integer totalInterviews = 0;

    @Column(name = "total_offers")
    private Integer totalOffers = 0;

    @Column(name = "placement_percentage")
    private Double placementPercentage = 0.0;

    @Column(name = "avg_package_lpa")
    private Double avgPackageLpa = 0.0;

    @Column(name = "max_package_lpa")
    private Double maxPackageLpa = 0.0;

    @Column(name = "generated_by")
    private String generatedBy;

    public WeeklyPlacementReport() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public College getCollege() { return college; }
    public void setCollege(College college) { this.college = college; }
    public String getReportTitle() { return reportTitle; }
    public void setReportTitle(String reportTitle) { this.reportTitle = reportTitle; }
    public LocalDate getReportingPeriodStart() { return reportingPeriodStart; }
    public void setReportingPeriodStart(LocalDate reportingPeriodStart) { this.reportingPeriodStart = reportingPeriodStart; }
    public LocalDate getReportingPeriodEnd() { return reportingPeriodEnd; }
    public void setReportingPeriodEnd(LocalDate reportingPeriodEnd) { this.reportingPeriodEnd = reportingPeriodEnd; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    public String getSnapshotJson() { return snapshotJson; }
    public void setSnapshotJson(String snapshotJson) { this.snapshotJson = snapshotJson; }
    public Integer getTotalDrives() { return totalDrives; }
    public void setTotalDrives(Integer totalDrives) { this.totalDrives = totalDrives; }
    public Integer getTotalApplications() { return totalApplications; }
    public void setTotalApplications(Integer totalApplications) { this.totalApplications = totalApplications; }
    public Integer getTotalShortlisted() { return totalShortlisted; }
    public void setTotalShortlisted(Integer totalShortlisted) { this.totalShortlisted = totalShortlisted; }
    public Integer getTotalInterviews() { return totalInterviews; }
    public void setTotalInterviews(Integer totalInterviews) { this.totalInterviews = totalInterviews; }
    public Integer getTotalOffers() { return totalOffers; }
    public void setTotalOffers(Integer totalOffers) { this.totalOffers = totalOffers; }
    public Double getPlacementPercentage() { return placementPercentage; }
    public void setPlacementPercentage(Double placementPercentage) { this.placementPercentage = placementPercentage; }
    public Double getAvgPackageLpa() { return avgPackageLpa; }
    public void setAvgPackageLpa(Double avgPackageLpa) { this.avgPackageLpa = avgPackageLpa; }
    public Double getMaxPackageLpa() { return maxPackageLpa; }
    public void setMaxPackageLpa(Double maxPackageLpa) { this.maxPackageLpa = maxPackageLpa; }
    public String getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(String generatedBy) { this.generatedBy = generatedBy; }
}

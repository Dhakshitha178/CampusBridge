package com.campusbridge.placement.repository;

import com.campusbridge.placement.entity.WeeklyPlacementReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeeklyPlacementReportRepository extends JpaRepository<WeeklyPlacementReport, Long> {
    List<WeeklyPlacementReport> findByCollegeIdOrderByGeneratedAtDesc(Long collegeId);
    Optional<WeeklyPlacementReport> findByCollegeIdAndReportingPeriodStartAndReportingPeriodEnd(
            Long collegeId, LocalDate start, LocalDate end);
}

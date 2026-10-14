package com.campusbridge.placement.repository;

import com.campusbridge.placement.entity.Interview;
import com.campusbridge.placement.entity.enums.InterviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Interview> findByStudentId(Long studentId);
    List<Interview> findByDriveId(Long driveId);
    List<Interview> findByInterviewDate(LocalDate date);
    List<Interview> findByStudentIdAndInterviewDate(Long studentId, LocalDate date);
    List<Interview> findByStatus(InterviewStatus status);

    @Query("SELECT i FROM Interview i WHERE i.drive.company.id = :companyId")
    List<Interview> findByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT i FROM Interview i WHERE i.student.college.id = :collegeId")
    List<Interview> findByCollegeId(@Param("collegeId") Long collegeId);

    // Conflict Check 1: Student overlapping interviews
    @Query("SELECT i FROM Interview i WHERE i.student.id = :studentId " +
           "AND i.interviewDate = :date " +
           "AND i.status != 'CANCELLED' " +
           "AND (:excludeId IS NULL OR i.id != :excludeId) " +
           "AND (i.startTime < :reqEnd AND i.endTime > :reqStart)")
    List<Interview> findStudentOverlappingInterviews(
            @Param("studentId") Long studentId,
            @Param("date") LocalDate date,
            @Param("reqStart") LocalTime reqStart,
            @Param("reqEnd") LocalTime reqEnd,
            @Param("excludeId") Long excludeId);

    // Conflict Check 2: Venue overlapping interviews (physical rooms)
    @Query("SELECT i FROM Interview i WHERE i.venue = :venue " +
           "AND i.mode = 'OFFLINE' " +
           "AND i.interviewDate = :date " +
           "AND i.status != 'CANCELLED' " +
           "AND (:excludeId IS NULL OR i.id != :excludeId) " +
           "AND (i.startTime < :reqEnd AND i.endTime > :reqStart)")
    List<Interview> findVenueOverlappingInterviews(
            @Param("venue") String venue,
            @Param("date") LocalDate date,
            @Param("reqStart") LocalTime reqStart,
            @Param("reqEnd") LocalTime reqEnd,
            @Param("excludeId") Long excludeId);

    // Conflict Check 3: Panel overlapping interviews
    @Query("SELECT i FROM Interview i WHERE i.panel.id = :panelId " +
           "AND i.interviewDate = :date " +
           "AND i.status != 'CANCELLED' " +
           "AND (:excludeId IS NULL OR i.id != :excludeId) " +
           "AND (i.startTime < :reqEnd AND i.endTime > :reqStart)")
    List<Interview> findPanelOverlappingInterviews(
            @Param("panelId") Long panelId,
            @Param("date") LocalDate date,
            @Param("reqStart") LocalTime reqStart,
            @Param("reqEnd") LocalTime reqEnd,
            @Param("excludeId") Long excludeId);

    long countByInterviewDate(LocalDate date);
}

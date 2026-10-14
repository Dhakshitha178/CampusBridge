package com.campusbridge.placement.repository;

import com.campusbridge.placement.entity.RescheduleRequest;
import com.campusbridge.placement.entity.enums.RescheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RescheduleRequestRepository extends JpaRepository<RescheduleRequest, Long> {
    List<RescheduleRequest> findByStudentId(Long studentId);
    List<RescheduleRequest> findByInterviewId(Long interviewId);
    List<RescheduleRequest> findByStatus(RescheduleStatus status);
    Optional<RescheduleRequest> findByInterviewIdAndStatus(Long interviewId, RescheduleStatus status);

    @Query("SELECT r FROM RescheduleRequest r WHERE r.student.college.id = :collegeId")
    List<RescheduleRequest> findByCollegeId(@Param("collegeId") Long collegeId);

    long countByStatus(RescheduleStatus status);
}

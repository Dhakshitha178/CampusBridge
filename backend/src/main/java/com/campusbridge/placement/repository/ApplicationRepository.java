package com.campusbridge.placement.repository;

import com.campusbridge.placement.entity.Application;
import com.campusbridge.placement.entity.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByStudentId(Long studentId);
    List<Application> findByDriveId(Long driveId);
    Optional<Application> findByDriveIdAndStudentId(Long driveId, Long studentId);
    boolean existsByDriveIdAndStudentId(Long driveId, Long studentId);

    List<Application> findByDriveIdAndStatus(Long driveId, ApplicationStatus status);
    List<Application> findByStatus(ApplicationStatus status);

    @Query("SELECT a FROM Application a WHERE a.drive.company.id = :companyId")
    List<Application> findByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT a FROM Application a WHERE a.student.college.id = :collegeId")
    List<Application> findByCollegeId(@Param("collegeId") Long collegeId);

    long countByDriveId(Long driveId);
    long countByDriveIdAndStatus(Long driveId, ApplicationStatus status);
    long countByStatus(ApplicationStatus status);
}

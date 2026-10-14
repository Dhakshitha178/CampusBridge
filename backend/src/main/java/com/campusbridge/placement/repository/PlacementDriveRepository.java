package com.campusbridge.placement.repository;

import com.campusbridge.placement.entity.PlacementDrive;
import com.campusbridge.placement.entity.enums.DriveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlacementDriveRepository extends JpaRepository<PlacementDrive, Long> {
    List<PlacementDrive> findByCompanyId(Long companyId);
    List<PlacementDrive> findByStatus(DriveStatus status);
    List<PlacementDrive> findByCollegeId(Long collegeId);
    List<PlacementDrive> findByStatusIn(List<DriveStatus> statuses);

    @Query("SELECT d FROM PlacementDrive d WHERE d.status = 'ACTIVE' OR d.status = 'APPROVED'")
    List<PlacementDrive> findActiveDrives();

    @Query("SELECT d FROM PlacementDrive d WHERE (d.status = 'ACTIVE' OR d.status = 'APPROVED') AND (d.college.id = :collegeId OR d.college IS NULL)")
    List<PlacementDrive> findActiveDrivesForCollege(@Param("collegeId") Long collegeId);

    long countByStatus(DriveStatus status);
}

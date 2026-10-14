package com.campusbridge.placement.repository;

import com.campusbridge.placement.entity.DriveEligibilityCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriveEligibilityCriteriaRepository extends JpaRepository<DriveEligibilityCriteria, Long> {
    Optional<DriveEligibilityCriteria> findByDriveId(Long driveId);
}

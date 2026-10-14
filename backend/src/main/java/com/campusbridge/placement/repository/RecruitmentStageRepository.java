package com.campusbridge.placement.repository;

import com.campusbridge.placement.entity.RecruitmentStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecruitmentStageRepository extends JpaRepository<RecruitmentStage, Long> {
    List<RecruitmentStage> findByDriveIdOrderByStageOrderAsc(Long driveId);
    Optional<RecruitmentStage> findByDriveIdAndStageOrder(Long driveId, Integer stageOrder);
    void deleteByDriveId(Long driveId);
}

package com.campusbridge.placement.repository;

import com.campusbridge.placement.entity.Offer;
import com.campusbridge.placement.entity.enums.OfferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    List<Offer> findByStudentId(Long studentId);
    List<Offer> findByCompanyId(Long companyId);
    List<Offer> findByDriveId(Long driveId);
    Optional<Offer> findByApplicationId(Long applicationId);
    boolean existsByApplicationId(Long applicationId);
    List<Offer> findByOfferStatus(OfferStatus status);

    @Query("SELECT o FROM Offer o WHERE o.student.college.id = :collegeId")
    List<Offer> findByCollegeId(@Param("collegeId") Long collegeId);

    long countByOfferStatus(OfferStatus status);
}

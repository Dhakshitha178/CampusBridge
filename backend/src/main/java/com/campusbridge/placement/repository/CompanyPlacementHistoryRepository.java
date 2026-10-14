package com.campusbridge.placement.repository;

import com.campusbridge.placement.entity.CompanyPlacementHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyPlacementHistoryRepository extends JpaRepository<CompanyPlacementHistory, Long> {
    List<CompanyPlacementHistory> findByCompanyId(Long companyId);
    List<CompanyPlacementHistory> findByCollegeId(Long collegeId);
    List<CompanyPlacementHistory> findByCompanyIdAndCollegeId(Long companyId, Long collegeId);

    @Query("SELECT cph FROM CompanyPlacementHistory cph WHERE cph.company.id = :companyId ORDER BY cph.academicYear DESC")
    List<CompanyPlacementHistory> findHistoryForCompany(@Param("companyId") Long companyId);
}

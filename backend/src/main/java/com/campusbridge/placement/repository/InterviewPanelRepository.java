package com.campusbridge.placement.repository;

import com.campusbridge.placement.entity.InterviewPanel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewPanelRepository extends JpaRepository<InterviewPanel, Long> {
    List<InterviewPanel> findByCompanyId(Long companyId);
}

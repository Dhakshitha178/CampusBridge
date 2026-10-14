package com.campusbridge.placement.repository;

import com.campusbridge.placement.entity.Interviewer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewerRepository extends JpaRepository<Interviewer, Long> {
    List<Interviewer> findByCompanyId(Long companyId);
}

package com.campusbridge.placement.repository;

import com.campusbridge.placement.entity.InterviewEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewEvaluationRepository extends JpaRepository<InterviewEvaluation, Long> {
    Optional<InterviewEvaluation> findByInterviewId(Long interviewId);
    List<InterviewEvaluation> findByApplicationId(Long applicationId);
    boolean existsByInterviewId(Long interviewId);
}

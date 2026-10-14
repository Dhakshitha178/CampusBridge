package com.campusbridge.placement.repository;

import com.campusbridge.placement.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByCollegeIdOrderByCreatedAtDesc(Long collegeId);

    @Query("SELECT a FROM Announcement a WHERE a.college.id = :collegeId OR a.college IS NULL ORDER BY a.createdAt DESC")
    List<Announcement> findAllForCollege(@Param("collegeId") Long collegeId);
}

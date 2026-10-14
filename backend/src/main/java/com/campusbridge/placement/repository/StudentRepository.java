package com.campusbridge.placement.repository;

import com.campusbridge.placement.entity.Student;
import com.campusbridge.placement.entity.enums.PlacementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUserId(Long userId);
    Optional<Student> findByRollNumber(String rollNumber);
    Optional<Student> findByUserEmail(String email);
    boolean existsByRollNumber(String rollNumber);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Student s WHERE s.user.email = :email")
    boolean existsByEmail(@Param("email") String email);

    List<Student> findByCollegeId(Long collegeId);
    List<Student> findByDepartment(String department);
    List<Student> findByCollegeIdAndDepartment(Long collegeId, String department);
    List<Student> findByPlacementStatus(PlacementStatus status);
    List<Student> findByGraduationYear(Integer graduationYear);

    long countByCollegeId(Long collegeId);
    long countByCollegeIdAndPlacementStatus(Long collegeId, PlacementStatus status);
}
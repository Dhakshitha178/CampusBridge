package com.campusbridge.placement.repository;

import com.campusbridge.placement.entity.College;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollegeRepository extends JpaRepository<College, Long> {
    Optional<College> findByCode(String code);
    boolean existsByCode(String code);
    Optional<College> findByName(String name);
    boolean existsByName(String name);
    List<College> findAllByOrderByNameAsc();
}

package com.campusbridge.placement;

import com.campusbridge.placement.dto.response.ConflictReportDto;
import com.campusbridge.placement.dto.response.EligibilityResultDto;
import com.campusbridge.placement.entity.DriveEligibilityCriteria;
import com.campusbridge.placement.entity.Student;
import com.campusbridge.placement.service.eligibility.EligibilityCheckService;
import com.campusbridge.placement.service.interview.SchedulingConflictService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PlacementBackendApplicationTests {

    @Autowired
    private EligibilityCheckService eligibilityCheckService;

    @Autowired
    private SchedulingConflictService conflictService;

    @Test
    @DisplayName("Application Context Loads Successfully")
    void contextLoads() {
        assertNotNull(eligibilityCheckService);
        assertNotNull(conflictService);
    }

    @Test
    @DisplayName("Explainable Eligibility Engine - Validates CGPA, Dept, Backlogs & Skills")
    void testEligibilityEngine() {
        Student eligibleStudent = new Student();
        eligibleStudent.setDepartment("CSE");
        eligibleStudent.setCgpa(8.5);
        eligibleStudent.setBacklogs(0);
        eligibleStudent.setGraduationYear(2026);
        eligibleStudent.setSkills("Java, Spring Boot, SQL, React");

        DriveEligibilityCriteria criteria = new DriveEligibilityCriteria();
        criteria.setAllowedDepartments("CSE,IT");
        criteria.setMinCgpa(7.5);
        criteria.setMaxBacklogs(0);
        criteria.setGraduationYear(2026);
        criteria.setRequiredSkills("Java, SQL");

        EligibilityResultDto result = eligibilityCheckService.checkEligibility(eligibleStudent, criteria);
        assertTrue(result.isEligible(), "Student should be eligible");
        assertEquals(100.0, result.getSkillMatchPercentage());

        // Ineligible Case: Low CGPA and extra backlogs
        Student ineligibleStudent = new Student();
        ineligibleStudent.setDepartment("MECH");
        ineligibleStudent.setCgpa(6.2);
        ineligibleStudent.setBacklogs(2);
        ineligibleStudent.setGraduationYear(2026);
        ineligibleStudent.setSkills("Python");

        EligibilityResultDto ineligResult = eligibilityCheckService.checkEligibility(ineligibleStudent, criteria);
        assertFalse(ineligResult.isEligible(), "Student should be ineligible");
        assertTrue(ineligResult.getReasons().size() >= 2, "Should record multiple explainable ineligibility reasons");
    }

    @Test
    @DisplayName("Interview Conflict Detection - Overlapping intervals detected accurately")
    void testConflictDetection() {
        // Pre-seeded Student 1 (Arun) has a Zoho interview at 10:00 - 11:00 on demo date
        LocalDate demoDate = LocalDate.now().plusDays(2);

        // Requested slot 10:30 - 11:30 overlaps with 10:00 - 11:00
        ConflictReportDto overlapReport = conflictService.checkConflicts(
                1L, demoDate, LocalTime.of(10, 30), LocalTime.of(11, 30),
                "ONLINE", null, null, null);

        assertTrue(overlapReport.isHasConflict(), "Should detect overlapping student interview");
        assertTrue(overlapReport.getConflicts().size() >= 1);
        assertEquals("STUDENT_CONFLICT", overlapReport.getConflicts().get(0).getConflictType());

        // Non-overlapping slot: 14:00 - 15:00 (well after morning interviews)
        ConflictReportDto nonOverlapReport = conflictService.checkConflicts(
                1L, demoDate, LocalTime.of(14, 0), LocalTime.of(15, 0),
                "ONLINE", null, null, null);

        assertFalse(nonOverlapReport.isHasConflict(), "14:00 - 15:00 interval must NOT conflict with morning interviews");
    }
}

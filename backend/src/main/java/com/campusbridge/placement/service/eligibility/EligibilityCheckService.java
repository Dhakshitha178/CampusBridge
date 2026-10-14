package com.campusbridge.placement.service.eligibility;

import com.campusbridge.placement.dto.response.EligibilityResultDto;
import com.campusbridge.placement.entity.DriveEligibilityCriteria;
import com.campusbridge.placement.entity.Student;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EligibilityCheckService {

    public EligibilityResultDto checkEligibility(Student student, DriveEligibilityCriteria criteria) {
        if (student == null) {
            return new EligibilityResultDto(false, "Student profile not found.");
        }
        if (criteria == null) {
            // No strict criteria defined => eligible by default
            EligibilityResultDto result = new EligibilityResultDto(true, "All registered students eligible.");
            result.setSkillMatchPercentage(100.0);
            return result;
        }

        EligibilityResultDto result = new EligibilityResultDto();
        List<String> ineligibilityReasons = new ArrayList<>();
        boolean isEligible = true;

        // 1. Department Check
        if (criteria.getAllowedDepartments() != null && !criteria.getAllowedDepartments().trim().isEmpty()) {
            Set<String> allowedDepts = Arrays.stream(criteria.getAllowedDepartments().split(","))
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .collect(Collectors.toSet());

            String studentDept = student.getDepartment() != null ? student.getDepartment().trim().toUpperCase() : "";
            if (!allowedDepts.contains(studentDept)) {
                isEligible = false;
                ineligibilityReasons.add(String.format("Department '%s' is not in eligible list [%s]",
                        student.getDepartment(), criteria.getAllowedDepartments()));
            }
        }

        // 2. CGPA Check
        if (criteria.getMinCgpa() != null) {
            double studentCgpa = student.getCgpa() != null ? student.getCgpa() : 0.0;
            if (studentCgpa < criteria.getMinCgpa()) {
                isEligible = false;
                ineligibilityReasons.add(String.format("CGPA %.2f is below minimum requirement of %.2f",
                        studentCgpa, criteria.getMinCgpa()));
            }
        }

        // 3. Backlogs Check
        if (criteria.getMaxBacklogs() != null) {
            int studentBacklogs = student.getBacklogs() != null ? student.getBacklogs() : 0;
            if (studentBacklogs > criteria.getMaxBacklogs()) {
                isEligible = false;
                ineligibilityReasons.add(String.format("Backlogs count (%d) exceeds allowable limit of %d",
                        studentBacklogs, criteria.getMaxBacklogs()));
            }
        }

        // 4. Graduation Year Check
        if (criteria.getGraduationYear() != null) {
            int studentGradYear = student.getGraduationYear() != null ? student.getGraduationYear() : 0;
            if (studentGradYear != criteria.getGraduationYear()) {
                isEligible = false;
                ineligibilityReasons.add(String.format("Graduation year %d does not match target batch %d",
                        studentGradYear, criteria.getGraduationYear()));
            }
        }

        // 5. Skills Matching
        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();
        double matchPercentage = 100.0;

        if (criteria.getRequiredSkills() != null && !criteria.getRequiredSkills().trim().isEmpty()) {
            List<String> requiredList = Arrays.stream(criteria.getRequiredSkills().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

            Set<String> studentSkillSet = new HashSet<>();
            if (student.getSkills() != null) {
                Arrays.stream(student.getSkills().split(","))
                        .map(String::trim)
                        .map(String::toLowerCase)
                        .forEach(studentSkillSet::add);
            }

            for (String req : requiredList) {
                if (studentSkillSet.contains(req.toLowerCase())) {
                    matchedSkills.add(req);
                } else {
                    missingSkills.add(req);
                }
            }

            if (!requiredList.isEmpty()) {
                matchPercentage = ((double) matchedSkills.size() / requiredList.size()) * 100.0;
            }
        }

        result.setEligible(isEligible);
        result.setReasons(ineligibilityReasons);
        result.setMatchedSkills(matchedSkills);
        result.setMissingSkills(missingSkills);
        result.setSkillMatchPercentage(Math.round(matchPercentage * 10.0) / 10.0);

        if (isEligible) {
            result.setSummary("Student satisfies all academic and departmental eligibility criteria.");
        } else {
            result.setSummary("Ineligible due to: " + String.join("; ", ineligibilityReasons));
        }

        return result;
    }
}

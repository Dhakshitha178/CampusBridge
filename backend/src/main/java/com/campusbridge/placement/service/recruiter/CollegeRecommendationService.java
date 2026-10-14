package com.campusbridge.placement.service.recruiter;

import com.campusbridge.placement.dto.response.CollegeRecommendationDto;
import com.campusbridge.placement.entity.College;
import com.campusbridge.placement.entity.CompanyPlacementHistory;
import com.campusbridge.placement.repository.CollegeRepository;
import com.campusbridge.placement.repository.CompanyPlacementHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CollegeRecommendationService {

    private final CollegeRepository collegeRepository;
    private final CompanyPlacementHistoryRepository historyRepository;

    // Configurable factor weights (sum to 1.0)
    private static final double WEIGHT_SELECTION_RATE = 0.35;
    private static final double WEIGHT_INTERVIEW_CONVERSION = 0.25;
    private static final double WEIGHT_AVG_PACKAGE = 0.20;
    private static final double WEIGHT_CANDIDATE_VOLUME = 0.20;

    public CollegeRecommendationService(CollegeRepository collegeRepository, CompanyPlacementHistoryRepository historyRepository) {
        this.collegeRepository = collegeRepository;
        this.historyRepository = historyRepository;
    }

    public List<CollegeRecommendationDto> getRecommendationsForCompany(Long companyId) {
        List<College> colleges = collegeRepository.findAll();
        List<CollegeRecommendationDto> recommendations = new ArrayList<>();

        for (College college : colleges) {
            recommendations.add(evaluateCollege(college, companyId));
        }

        // Sort descending by priority score
        recommendations.sort((a, b) -> Double.compare(b.getPriorityScore(), a.getPriorityScore()));
        return recommendations;
    }

    private CollegeRecommendationDto evaluateCollege(College college, Long companyId) {
        CollegeRecommendationDto dto = new CollegeRecommendationDto();
        dto.setCollegeId(college.getId());
        dto.setCollegeName(college.getName());
        dto.setState(college.getState());

        List<CompanyPlacementHistory> historyList = companyId != null
                ? historyRepository.findByCompanyIdAndCollegeId(companyId, college.getId())
                : historyRepository.findByCollegeId(college.getId());

        int drivesCount = historyList.size();
        dto.setHistoricalDrivesCount(drivesCount);

        int totalApplicants = historyList.stream().mapToInt(h -> h.getApplicantCount() != null ? h.getApplicantCount() : 0).sum();
        int totalShortlisted = historyList.stream().mapToInt(h -> h.getShortlistedCount() != null ? h.getShortlistedCount() : 0).sum();
        int totalSelected = historyList.stream().mapToInt(h -> h.getSelectedCount() != null ? h.getSelectedCount() : 0).sum();
        double sumAvgPkg = historyList.stream().mapToDouble(h -> h.getAvgPackage() != null ? h.getAvgPackage() : 0.0).sum();

        dto.setTotalHistoricalApplicants(totalApplicants);

        // Data adequacy check: Minimum 1 historical drive and at least 5 applicants
        if (drivesCount < 1 || totalApplicants < 5) {
            dto.setSufficientData(false);
            dto.setPriorityScore(0.0);
            dto.setPriorityTier("INSUFFICIENT DATA");
            dto.setDataLimitations(String.format("Only %d historical drive(s) and %d total candidate records found. Need >= 1 drive and >= 5 candidates for reliable evaluation.", drivesCount, totalApplicants));
            dto.setExplanation("Insufficient historical recruitment history to calculate a statistically sound recommendation score.");
            return dto;
        }

        dto.setSufficientData(true);

        // 1. Selection Rate Score (0 - 100)
        double selectionRate = totalApplicants > 0 ? ((double) totalSelected / totalApplicants) * 100.0 : 0.0;
        double normSelectionScore = Math.min(100.0, selectionRate * 4.0); // e.g. 25% selection rate yields 100 score

        // 2. Interview Conversion Score (0 - 100)
        double conversionRate = totalShortlisted > 0 ? ((double) totalSelected / totalShortlisted) * 100.0 : 0.0;
        double normConversionScore = Math.min(100.0, conversionRate * 2.0); // e.g. 50% conversion yields 100 score

        // 3. Package Score (0 - 100)
        double avgPackage = drivesCount > 0 ? sumAvgPkg / drivesCount : 0.0;
        double normPackageScore = Math.min(100.0, (avgPackage / 15.0) * 100.0); // 15 LPA benchmark

        // 4. Candidate Volume Adequacy (0 - 100)
        double normVolumeScore = Math.min(100.0, (totalApplicants / 100.0) * 100.0);

        Map<String, Double> factors = new HashMap<>();
        factors.put("Selection Rate (" + String.format("%.1f", selectionRate) + "%)", Math.round(normSelectionScore * 10.0) / 10.0);
        factors.put("Interview Conversion (" + String.format("%.1f", conversionRate) + "%)", Math.round(normConversionScore * 10.0) / 10.0);
        factors.put("Package Benchmark (" + String.format("%.1f", avgPackage) + " LPA)", Math.round(normPackageScore * 10.0) / 10.0);
        factors.put("Candidate Volume Pool", Math.round(normVolumeScore * 10.0) / 10.0);
        dto.setFactorScores(factors);

        double compositeScore = (normSelectionScore * WEIGHT_SELECTION_RATE)
                + (normConversionScore * WEIGHT_INTERVIEW_CONVERSION)
                + (normPackageScore * WEIGHT_AVG_PACKAGE)
                + (normVolumeScore * WEIGHT_CANDIDATE_VOLUME);

        compositeScore = Math.round(compositeScore * 10.0) / 10.0;
        dto.setPriorityScore(compositeScore);

        List<String> pos = new ArrayList<>();
        List<String> neg = new ArrayList<>();

        if (selectionRate >= 20.0) {
            pos.add(String.format("Strong past selection rate of %.1f%% across %d drive cycles.", selectionRate, drivesCount));
        } else if (selectionRate < 10.0) {
            neg.add(String.format("Historical selection rate is modest (%.1f%%).", selectionRate));
        }

        if (conversionRate >= 45.0) {
            pos.add(String.format("High final interview conversion rate (%.1f%% of shortlisted students cleared).", conversionRate));
        } else {
            neg.add("Candidate drop-off observed during advanced technical and managerial rounds.");
        }

        if (totalApplicants >= 50) {
            pos.add(String.format("Large qualified candidate talent pool (%d applicants).", totalApplicants));
        }

        if (compositeScore >= 75.0) {
            dto.setPriorityTier("HIGH PRIORITY");
            dto.setExplanation(String.format("%s is designated as HIGH PRIORITY based on consistent selection conversion (%.1f%%) and robust candidate pool across %d historical recruitment seasons.",
                    college.getName(), selectionRate, drivesCount));
        } else if (compositeScore >= 50.0) {
            dto.setPriorityTier("MEDIUM PRIORITY");
            dto.setExplanation(String.format("%s exhibits stable baseline conversion with moderate technical round clearance. Recommended for targeted technical roles.", college.getName()));
        } else {
            dto.setPriorityTier("LOW PRIORITY");
            dto.setExplanation(String.format("%s demonstrated lower historical conversion rates. Recommend foundational skill development before scheduling on-campus drives.", college.getName()));
        }

        dto.setPositiveEvidence(pos);
        dto.setNegativeEvidence(neg);
        dto.setDataLimitations(String.format("Evaluation synthesized from %d verified placement seasons. Individual branch breakdowns may vary.", drivesCount));

        return dto;
    }
}

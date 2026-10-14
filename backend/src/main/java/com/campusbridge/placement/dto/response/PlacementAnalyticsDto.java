package com.campusbridge.placement.dto.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlacementAnalyticsDto {
    private double placementPercentage;
    private double applicationToShortlistRate;
    private double overallSelectionRate;
    private double avgPackageLpa;
    private double maxPackageLpa;
    private long totalStudents;
    private long placedStudents;
    private long totalApplications;
    private long totalOffers;

    // Charts data
    private Map<String, Long> departmentWiseSelections = new HashMap<>();
    private Map<String, Long> companyWiseSelections = new HashMap<>();
    private Map<String, Double> companyWiseAvgPackage = new HashMap<>();
    private Map<String, Long> roundWiseConversion = new HashMap<>();
    private List<YearlyTrendDto> historicalTrends = new ArrayList<>();
    private List<CollegePerformanceDto> collegeWiseComparison = new ArrayList<>();
    private List<StateAggregatedDto> stateWiseAggregated = new ArrayList<>();

    public static class YearlyTrendDto {
        public String academicYear;
        public long registeredStudents;
        public long placedStudents;
        public double placementPercentage;
        public double avgPackage;
        public double highestPackage;

        public YearlyTrendDto() {}
        public YearlyTrendDto(String year, long reg, long placed, double pct, double avgPkg, double highPkg) {
            this.academicYear = year;
            this.registeredStudents = reg;
            this.placedStudents = placed;
            this.placementPercentage = pct;
            this.avgPackage = avgPkg;
            this.highestPackage = highPkg;
        }
    }

    public static class CollegePerformanceDto {
        public Long collegeId;
        public String collegeName;
        public String state;
        public long totalApplicants;
        public long shortlistedCount;
        public long selectedCount;
        public double selectionRate;
        public double avgPackage;

        public CollegePerformanceDto() {}
        public CollegePerformanceDto(Long id, String name, String state, long applicants, long shortlisted, long selected, double rate, double avgPkg) {
            this.collegeId = id;
            this.collegeName = name;
            this.state = state;
            this.totalApplicants = applicants;
            this.shortlistedCount = shortlisted;
            this.selectedCount = selected;
            this.selectionRate = rate;
            this.avgPackage = avgPkg;
        }
    }

    public static class StateAggregatedDto {
        public String state;
        public long participatingColleges;
        public long totalApplicants;
        public long totalOffers;
        public double avgSelectionRate;

        public StateAggregatedDto() {}
        public StateAggregatedDto(String state, long colleges, long applicants, long offers, double rate) {
            this.state = state;
            this.participatingColleges = colleges;
            this.totalApplicants = applicants;
            this.totalOffers = offers;
            this.avgSelectionRate = rate;
        }
    }

    public PlacementAnalyticsDto() {}

    public double getPlacementPercentage() { return placementPercentage; }
    public void setPlacementPercentage(double placementPercentage) { this.placementPercentage = placementPercentage; }
    public double getApplicationToShortlistRate() { return applicationToShortlistRate; }
    public void setApplicationToShortlistRate(double applicationToShortlistRate) { this.applicationToShortlistRate = applicationToShortlistRate; }
    public double getOverallSelectionRate() { return overallSelectionRate; }
    public void setOverallSelectionRate(double overallSelectionRate) { this.overallSelectionRate = overallSelectionRate; }
    public double getAvgPackageLpa() { return avgPackageLpa; }
    public void setAvgPackageLpa(double avgPackageLpa) { this.avgPackageLpa = avgPackageLpa; }
    public double getMaxPackageLpa() { return maxPackageLpa; }
    public void setMaxPackageLpa(double maxPackageLpa) { this.maxPackageLpa = maxPackageLpa; }
    public long getTotalStudents() { return totalStudents; }
    public void setTotalStudents(long totalStudents) { this.totalStudents = totalStudents; }
    public long getPlacedStudents() { return placedStudents; }
    public void setPlacedStudents(long placedStudents) { this.placedStudents = placedStudents; }
    public long getTotalApplications() { return totalApplications; }
    public void setTotalApplications(long totalApplications) { this.totalApplications = totalApplications; }
    public long getTotalOffers() { return totalOffers; }
    public void setTotalOffers(long totalOffers) { this.totalOffers = totalOffers; }
    public Map<String, Long> getDepartmentWiseSelections() { return departmentWiseSelections; }
    public void setDepartmentWiseSelections(Map<String, Long> departmentWiseSelections) { this.departmentWiseSelections = departmentWiseSelections; }
    public Map<String, Long> getCompanyWiseSelections() { return companyWiseSelections; }
    public void setCompanyWiseSelections(Map<String, Long> companyWiseSelections) { this.companyWiseSelections = companyWiseSelections; }
    public Map<String, Double> getCompanyWiseAvgPackage() { return companyWiseAvgPackage; }
    public void setCompanyWiseAvgPackage(Map<String, Double> companyWiseAvgPackage) { this.companyWiseAvgPackage = companyWiseAvgPackage; }
    public Map<String, Long> getRoundWiseConversion() { return roundWiseConversion; }
    public void setRoundWiseConversion(Map<String, Long> roundWiseConversion) { this.roundWiseConversion = roundWiseConversion; }
    public List<YearlyTrendDto> getHistoricalTrends() { return historicalTrends; }
    public void setHistoricalTrends(List<YearlyTrendDto> historicalTrends) { this.historicalTrends = historicalTrends; }
    public List<CollegePerformanceDto> getCollegeWiseComparison() { return collegeWiseComparison; }
    public void setCollegeWiseComparison(List<CollegePerformanceDto> collegeWiseComparison) { this.collegeWiseComparison = collegeWiseComparison; }
    public List<StateAggregatedDto> getStateWiseAggregated() { return stateWiseAggregated; }
    public void setStateWiseAggregated(List<StateAggregatedDto> stateWiseAggregated) { this.stateWiseAggregated = stateWiseAggregated; }
}

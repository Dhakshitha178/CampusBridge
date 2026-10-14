package com.campusbridge.placement.dto.response;

import java.util.ArrayList;
import java.util.List;

public class EligibilityResultDto {
    private boolean eligible;
    private List<String> reasons = new ArrayList<>();
    private List<String> missingSkills = new ArrayList<>();
    private List<String> matchedSkills = new ArrayList<>();
    private Double skillMatchPercentage = 100.0;
    private String summary;

    public EligibilityResultDto() {}

    public EligibilityResultDto(boolean eligible, String summary) {
        this.eligible = eligible;
        this.summary = summary;
    }

    public boolean isEligible() { return eligible; }
    public void setEligible(boolean eligible) { this.eligible = eligible; }
    public List<String> getReasons() { return reasons; }
    public void setReasons(List<String> reasons) { this.reasons = reasons; }
    public List<String> getMissingSkills() { return missingSkills; }
    public void setMissingSkills(List<String> missingSkills) { this.missingSkills = missingSkills; }
    public List<String> getMatchedSkills() { return matchedSkills; }
    public void setMatchedSkills(List<String> matchedSkills) { this.matchedSkills = matchedSkills; }
    public Double getSkillMatchPercentage() { return skillMatchPercentage; }
    public void setSkillMatchPercentage(Double skillMatchPercentage) { this.skillMatchPercentage = skillMatchPercentage; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
}

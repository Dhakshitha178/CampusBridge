package com.campusbridge.placement.dto.response;

import java.util.ArrayList;
import java.util.List;

public class ConflictReportDto {
    private boolean hasConflict;
    private List<ConflictDetailDto> conflicts = new ArrayList<>();
    private String summary;

    public ConflictReportDto() {}

    public ConflictReportDto(boolean hasConflict, String summary) {
        this.hasConflict = hasConflict;
        this.summary = summary;
    }

    public boolean isHasConflict() { return hasConflict; }
    public void setHasConflict(boolean hasConflict) { this.hasConflict = hasConflict; }
    public List<ConflictDetailDto> getConflicts() { return conflicts; }
    public void setConflicts(List<ConflictDetailDto> conflicts) { this.conflicts = conflicts; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
}

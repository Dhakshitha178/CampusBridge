package com.campusbridge.placement.dto.request;

import com.campusbridge.placement.entity.enums.TargetAudience;

public class AnnouncementCreateRequest {
    private String title;
    private String content;
    private TargetAudience targetAudience = TargetAudience.ALL;
    private String targetDepartment;
    private Long companyId;

    public AnnouncementCreateRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public TargetAudience getTargetAudience() { return targetAudience; }
    public void setTargetAudience(TargetAudience targetAudience) { this.targetAudience = targetAudience; }
    public String getTargetDepartment() { return targetDepartment; }
    public void setTargetDepartment(String targetDepartment) { this.targetDepartment = targetDepartment; }
    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }
}

package com.campusbridge.placement.dto.response;

import com.campusbridge.placement.entity.enums.Role;

public class AuthResponse {
    private String token;
    private Long userId;
    private String email;
    private String fullName;
    private Role role;
    private Long profileId; // studentProfileId or recruiterProfileId
    private Long collegeId;
    private String collegeName;
    private Long companyId;
    private String companyName;
    private String approvalStatus = "APPROVED";
    private Boolean emailVerified = true;
    private String designation;

    public AuthResponse() {}

    public AuthResponse(String token, Long userId, String email, String fullName, Role role, Long profileId, Long collegeId, String collegeName, Long companyId, String companyName) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.profileId = profileId;
        this.collegeId = collegeId;
        this.collegeName = collegeName;
        this.companyId = companyId;
        this.companyName = companyName;
    }

    public AuthResponse(String token, Long userId, String email, String fullName, Role role, Long profileId, Long collegeId, String collegeName, Long companyId, String companyName, String approvalStatus, Boolean emailVerified, String designation) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.profileId = profileId;
        this.collegeId = collegeId;
        this.collegeName = collegeName;
        this.companyId = companyId;
        this.companyName = companyName;
        this.approvalStatus = approvalStatus;
        this.emailVerified = emailVerified;
        this.designation = designation;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public Long getProfileId() { return profileId; }
    public void setProfileId(Long profileId) { this.profileId = profileId; }
    public Long getCollegeId() { return collegeId; }
    public void setCollegeId(Long collegeId) { this.collegeId = collegeId; }
    public String getCollegeName() { return collegeName; }
    public void setCollegeName(String collegeName) { this.collegeName = collegeName; }
    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }
    public Boolean getEmailVerified() { return emailVerified; }
    public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
}
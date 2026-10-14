package com.campusbridge.placement.dto.response;

import com.campusbridge.placement.entity.Student;
import com.campusbridge.placement.entity.enums.PlacementStatus;

public class StudentResponse {
    private Long id;
    private Long userId;
    private String fullName;
    private String rollNumber;
    private String email;
    private String phoneNumber;
    private String department;
    private String academicYear;
    private Integer graduationYear;
    private Double cgpa;
    private Integer backlogs;
    private String skills;
    private String certifications;
    private String resumeUrl;
    private String resumeFileName;
    private PlacementStatus placementStatus;
    private Long collegeId;
    private String collegeName;

    public StudentResponse() {}

    public StudentResponse(Student s) {
        this.id = s.getId();
        this.userId = s.getUser() != null ? s.getUser().getId() : null;
        this.fullName = s.getFullName();
        this.rollNumber = s.getRollNumber();
        this.email = s.getEmail();
        this.phoneNumber = s.getUser() != null ? s.getUser().getPhoneNumber() : null;
        this.department = s.getDepartment();
        this.academicYear = s.getAcademicYear();
        this.graduationYear = s.getGraduationYear();
        this.cgpa = s.getCgpa();
        this.backlogs = s.getBacklogs();
        this.skills = s.getSkills();
        this.certifications = s.getCertifications();
        this.resumeUrl = s.getResumeUrl();
        this.resumeFileName = s.getResumeFileName();
        this.placementStatus = s.getPlacementStatus();
        this.collegeId = s.getCollege() != null ? s.getCollege().getId() : null;
        this.collegeName = s.getCollege() != null ? s.getCollege().getName() : null;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    public Integer getGraduationYear() { return graduationYear; }
    public void setGraduationYear(Integer graduationYear) { this.graduationYear = graduationYear; }
    public Double getCgpa() { return cgpa; }
    public void setCgpa(Double cgpa) { this.cgpa = cgpa; }
    public Integer getBacklogs() { return backlogs; }
    public void setBacklogs(Integer backlogs) { this.backlogs = backlogs; }
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    public String getCertifications() { return certifications; }
    public void setCertifications(String certifications) { this.certifications = certifications; }
    public String getResumeUrl() { return resumeUrl; }
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }
    public String getResumeFileName() { return resumeFileName; }
    public void setResumeFileName(String resumeFileName) { this.resumeFileName = resumeFileName; }
    public PlacementStatus getPlacementStatus() { return placementStatus; }
    public void setPlacementStatus(PlacementStatus placementStatus) { this.placementStatus = placementStatus; }
    public Long getCollegeId() { return collegeId; }
    public void setCollegeId(Long collegeId) { this.collegeId = collegeId; }
    public String getCollegeName() { return collegeName; }
    public void setCollegeName(String collegeName) { this.collegeName = collegeName; }
}
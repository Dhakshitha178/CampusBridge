-- CampusBridge Database Schema
-- MySQL 8.0+ Compatible

CREATE DATABASE IF NOT EXISTS campus_bridge_db;
USE campus_bridge_db;

-- 1. Colleges
CREATE TABLE IF NOT EXISTS colleges (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    address TEXT,
    contact_email VARCHAR(100),
    contact_phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Departments
CREATE TABLE IF NOT EXISTS departments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    college_id BIGINT,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_departments_college FOREIGN KEY (college_id) REFERENCES colleges(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Users
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('STUDENT', 'TPO', 'RECRUITER') NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    phone_number VARCHAR(20),
    college_id BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_college FOREIGN KEY (college_id) REFERENCES colleges(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Companies
CREATE TABLE IF NOT EXISTS companies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    logo_url VARCHAR(500),
    website VARCHAR(255),
    industry VARCHAR(100),
    description TEXT,
    address TEXT,
    city VARCHAR(100),
    state VARCHAR(100),
    hr_contact_name VARCHAR(100),
    hr_contact_email VARCHAR(100),
    hr_contact_phone VARCHAR(20),
    verified BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Student Profiles
CREATE TABLE IF NOT EXISTS student_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,
    college_id BIGINT NOT NULL,
    roll_number VARCHAR(50) UNIQUE NOT NULL,
    department VARCHAR(100) NOT NULL,
    academic_year VARCHAR(50) NOT NULL,
    graduation_year INT NOT NULL,
    cgpa DOUBLE NOT NULL,
    backlogs INT DEFAULT 0,
    skills TEXT,
    certifications TEXT,
    resume_url VARCHAR(500),
    resume_file_name VARCHAR(255),
    placement_status ENUM('UNPLACED', 'PLACED', 'OPTED_OUT') DEFAULT 'UNPLACED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_students_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_students_college FOREIGN KEY (college_id) REFERENCES colleges(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. Recruiter Profiles
CREATE TABLE IF NOT EXISTS recruiter_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,
    company_id BIGINT NOT NULL,
    designation VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_recruiters_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_recruiters_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. Placement Drives
CREATE TABLE IF NOT EXISTS placement_drives (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_id BIGINT NOT NULL,
    college_id BIGINT,
    job_title VARCHAR(150) NOT NULL,
    job_description TEXT NOT NULL,
    package_lpa DOUBLE NOT NULL,
    location VARCHAR(100) NOT NULL,
    work_mode ENUM('ON_SITE', 'HYBRID', 'REMOTE') DEFAULT 'ON_SITE',
    vacancies INT NOT NULL,
    employment_type ENUM('FULL_TIME', 'INTERNSHIP', 'INTERN_TO_FTE') DEFAULT 'FULL_TIME',
    application_deadline DATETIME NOT NULL,
    drive_date DATE NOT NULL,
    status ENUM('DRAFT', 'PENDING_APPROVAL', 'APPROVED', 'REJECTED', 'ACTIVE', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING_APPROVAL',
    tpo_approval_notes TEXT,
    created_by_user_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_drives_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE,
    CONSTRAINT fk_drives_college FOREIGN KEY (college_id) REFERENCES colleges(id) ON DELETE SET NULL,
    CONSTRAINT fk_drives_user FOREIGN KEY (created_by_user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. Drive Eligibility Criteria
CREATE TABLE IF NOT EXISTS drive_eligibility_criteria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    drive_id BIGINT UNIQUE NOT NULL,
    allowed_departments TEXT,
    min_cgpa DOUBLE NOT NULL,
    max_backlogs INT NOT NULL,
    graduation_year INT NOT NULL,
    required_skills TEXT,
    CONSTRAINT fk_criteria_drive FOREIGN KEY (drive_id) REFERENCES placement_drives(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. Recruitment Stages (Dynamic Per Drive)
CREATE TABLE IF NOT EXISTS recruitment_stages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    drive_id BIGINT NOT NULL,
    stage_order INT NOT NULL,
    stage_name VARCHAR(100) NOT NULL,
    stage_type VARCHAR(50) NOT NULL,
    description TEXT,
    cut_off_score DOUBLE,
    is_final_stage BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_stages_drive FOREIGN KEY (drive_id) REFERENCES placement_drives(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. Applications
CREATE TABLE IF NOT EXISTS applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    drive_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    status ENUM('APPLIED', 'UNDER_REVIEW', 'SHORTLISTED', 'ASSESSMENT', 'INTERVIEW', 'SELECTED', 'REJECTED', 'NOT_SELECTED', 'WITHDRAWN') DEFAULT 'APPLIED',
    current_stage_id BIGINT,
    applied_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    notes TEXT,
    UNIQUE KEY unique_student_drive (drive_id, student_id),
    CONSTRAINT fk_applications_drive FOREIGN KEY (drive_id) REFERENCES placement_drives(id) ON DELETE CASCADE,
    CONSTRAINT fk_applications_student FOREIGN KEY (student_id) REFERENCES student_profiles(id) ON DELETE CASCADE,
    CONSTRAINT fk_applications_stage FOREIGN KEY (current_stage_id) REFERENCES recruitment_stages(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. Application Status History
CREATE TABLE IF NOT EXISTS application_status_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    stage_name VARCHAR(100),
    remarks TEXT,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    changed_by VARCHAR(100),
    CONSTRAINT fk_history_application FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 12. Interviewers
CREATE TABLE IF NOT EXISTS interviewers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    designation VARCHAR(100),
    phone VARCHAR(20),
    CONSTRAINT fk_interviewers_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 13. Interview Panels
CREATE TABLE IF NOT EXISTS interview_panels (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_id BIGINT NOT NULL,
    panel_name VARCHAR(100) NOT NULL,
    description TEXT,
    CONSTRAINT fk_panels_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 14. Panel Members
CREATE TABLE IF NOT EXISTS interview_panel_members (
    panel_id BIGINT NOT NULL,
    interviewer_id BIGINT NOT NULL,
    PRIMARY KEY (panel_id, interviewer_id),
    CONSTRAINT fk_members_panel FOREIGN KEY (panel_id) REFERENCES interview_panels(id) ON DELETE CASCADE,
    CONSTRAINT fk_members_interviewer FOREIGN KEY (interviewer_id) REFERENCES interviewers(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 15. Interviews
CREATE TABLE IF NOT EXISTS interviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    drive_id BIGINT NOT NULL,
    stage_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    interview_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    mode ENUM('ONLINE', 'OFFLINE') DEFAULT 'ONLINE',
    meeting_link VARCHAR(500),
    venue VARCHAR(200),
    panel_id BIGINT,
    status ENUM('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'RESCHEDULE_REQUESTED', 'RESCHEDULED') DEFAULT 'SCHEDULED',
    instructions TEXT,
    required_documents TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_interviews_drive FOREIGN KEY (drive_id) REFERENCES placement_drives(id) ON DELETE CASCADE,
    CONSTRAINT fk_interviews_stage FOREIGN KEY (stage_id) REFERENCES recruitment_stages(id) ON DELETE CASCADE,
    CONSTRAINT fk_interviews_student FOREIGN KEY (student_id) REFERENCES student_profiles(id) ON DELETE CASCADE,
    CONSTRAINT fk_interviews_panel FOREIGN KEY (panel_id) REFERENCES interview_panels(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 16. Interview Evaluations
CREATE TABLE IF NOT EXISTS interview_evaluations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    interview_id BIGINT NOT NULL,
    application_id BIGINT NOT NULL,
    technical_score DOUBLE,
    communication_score DOUBLE,
    problem_solving_score DOUBLE,
    overall_rating DOUBLE,
    comments TEXT,
    decision ENUM('PASS', 'FAIL', 'HOLD') NOT NULL,
    evaluated_by VARCHAR(100),
    evaluated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_evaluations_interview FOREIGN KEY (interview_id) REFERENCES interviews(id) ON DELETE CASCADE,
    CONSTRAINT fk_evaluations_application FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 17. Reschedule Requests
CREATE TABLE IF NOT EXISTS reschedule_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    interview_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    reason TEXT NOT NULL,
    preferred_alternative_time VARCHAR(255),
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    tpo_remarks TEXT,
    proposed_new_date DATE,
    proposed_new_start_time TIME,
    proposed_new_end_time TIME,
    reviewed_by VARCHAR(100),
    reviewed_at DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reschedule_interview FOREIGN KEY (interview_id) REFERENCES interviews(id) ON DELETE CASCADE,
    CONSTRAINT fk_reschedule_student FOREIGN KEY (student_id) REFERENCES student_profiles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 18. Offers
CREATE TABLE IF NOT EXISTS offers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id BIGINT UNIQUE NOT NULL,
    student_id BIGINT NOT NULL,
    company_id BIGINT NOT NULL,
    drive_id BIGINT NOT NULL,
    job_role VARCHAR(150) NOT NULL,
    package_lpa DOUBLE NOT NULL,
    location VARCHAR(100),
    joining_date DATE,
    offer_letter_url VARCHAR(500),
    offer_status ENUM('OFFERED', 'ACCEPTED', 'REJECTED', 'WITHDRAWN') DEFAULT 'OFFERED',
    offered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_offers_application FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE,
    CONSTRAINT fk_offers_student FOREIGN KEY (student_id) REFERENCES student_profiles(id) ON DELETE CASCADE,
    CONSTRAINT fk_offers_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE,
    CONSTRAINT fk_offers_drive FOREIGN KEY (drive_id) REFERENCES placement_drives(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 19. Notifications
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    reference_id BIGINT,
    reference_type VARCHAR(50),
    read_status BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 20. Announcements
CREATE TABLE IF NOT EXISTS announcements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    college_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    target_audience ENUM('ALL', 'STUDENTS', 'RECRUITERS', 'DEPARTMENT') DEFAULT 'ALL',
    target_department VARCHAR(100),
    company_id BIGINT,
    posted_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_announcements_college FOREIGN KEY (college_id) REFERENCES colleges(id) ON DELETE CASCADE,
    CONSTRAINT fk_announcements_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 21. Weekly Placement Reports
CREATE TABLE IF NOT EXISTS weekly_placement_reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    college_id BIGINT NOT NULL,
    report_title VARCHAR(255) NOT NULL,
    reporting_period_start DATE NOT NULL,
    reporting_period_end DATE NOT NULL,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    snapshot_json LONGTEXT,
    total_drives INT DEFAULT 0,
    total_applications INT DEFAULT 0,
    total_shortlisted INT DEFAULT 0,
    total_interviews INT DEFAULT 0,
    total_offers INT DEFAULT 0,
    placement_percentage DOUBLE DEFAULT 0.0,
    avg_package_lpa DOUBLE DEFAULT 0.0,
    max_package_lpa DOUBLE DEFAULT 0.0,
    generated_by VARCHAR(100),
    CONSTRAINT fk_reports_college FOREIGN KEY (college_id) REFERENCES colleges(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 22. Company Placement History (Aggregated Historical Records)
CREATE TABLE IF NOT EXISTS company_placement_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_id BIGINT NOT NULL,
    college_id BIGINT NOT NULL,
    academic_year VARCHAR(50) NOT NULL,
    applicant_count INT DEFAULT 0,
    shortlisted_count INT DEFAULT 0,
    selected_count INT DEFAULT 0,
    roles_offered VARCHAR(255),
    avg_package DOUBLE DEFAULT 0.0,
    highest_package DOUBLE DEFAULT 0.0,
    CONSTRAINT fk_cph_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE,
    CONSTRAINT fk_cph_college FOREIGN KEY (college_id) REFERENCES colleges(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 23. Audit Logs
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_name VARCHAR(100) NOT NULL,
    entity_id BIGINT,
    action VARCHAR(100) NOT NULL,
    performed_by VARCHAR(100) NOT NULL,
    details TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

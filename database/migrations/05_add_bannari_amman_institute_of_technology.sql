-- Migration 05: Add Bannari Amman Institute of Technology (BIT)
-- Idempotent script: avoids duplicate entries if already present.

-- 1. Insert College
INSERT INTO colleges (name, code, city, state, contact_email, contact_phone, created_at)
SELECT 'Bannari Amman Institute of Technology', 'BIT-SAT', 'Sathyamangalam', 'Tamil Nadu', 'placement@bitsathy.ac.in', '+91 4295 226000', NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM colleges WHERE name = 'Bannari Amman Institute of Technology' OR code = 'BIT-SAT'
);

-- 2. Insert Departments for BIT
INSERT INTO departments (college_id, name, code, created_at)
SELECT c.id, d.name, d.code, NOW()
FROM colleges c
CROSS JOIN (
    SELECT 'Computer Science & Engineering' AS name, 'CSE' AS code
    UNION ALL SELECT 'Information Technology', 'IT'
    UNION ALL SELECT 'Electronics & Communication Engineering', 'ECE'
    UNION ALL SELECT 'Electrical & Electronics Engineering', 'EEE'
    UNION ALL SELECT 'Mechanical Engineering', 'MECH'
    UNION ALL SELECT 'Artificial Intelligence & Data Science', 'AIDS'
) d
WHERE (c.name = 'Bannari Amman Institute of Technology' OR c.code = 'BIT-SAT')
AND NOT EXISTS (
    SELECT 1 FROM departments WHERE college_id = c.id AND code = d.code
);

-- 3. Insert BIT TPO User (Default Demo Password: Tpo@2025)
INSERT INTO users (email, password, role, full_name, phone_number, designation, college_id, is_active, approval_status, is_email_verified)
SELECT 'tpo@bitsathy.ac.in', '$2a$10$dwG8g5MA4Yr0YUh8pdwrAO4lLNXLf8PHHSxra//o8XtaewJP94ozK', 'TPO', 'Dr. S. BIT Placement Officer', '+91 9842200029', 'Training and Placement Officer', c.id, 1, 'APPROVED', 1
FROM colleges c
WHERE (c.name = 'Bannari Amman Institute of Technology' OR c.code = 'BIT-SAT')
AND NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'tpo@bitsathy.ac.in'
);

-- 4. Insert Company Placement History for BIT
INSERT INTO company_placement_history (company_id, college_id, academic_year, applicant_count, shortlisted_count, selected_count, roles_offered, avg_package, highest_package)
SELECT comp.id, col.id, hist.academic_year, hist.applicant_count, hist.shortlisted_count, hist.selected_count, hist.roles_offered, hist.avg_package, hist.highest_package
FROM colleges col
CROSS JOIN (
    SELECT 1 as comp_id, '2024-2025' as academic_year, 92 as applicant_count, 46 as shortlisted_count, 22 as selected_count, 'MTS, Product Developer' as roles_offered, 8.6 as avg_package, 11.5 as highest_package
    UNION ALL SELECT 2, '2024-2025', 135, 72, 40, 'Digital, Ninja', 6.8, 7.5
    UNION ALL SELECT 3, '2024-2025', 115, 50, 24, 'Specialist Programmer, SE', 8.0, 9.5
) hist
JOIN companies comp ON comp.id = hist.comp_id
WHERE (col.name = 'Bannari Amman Institute of Technology' OR col.code = 'BIT-SAT')
AND NOT EXISTS (
    SELECT 1 FROM company_placement_history WHERE college_id = col.id AND company_id = comp.id AND academic_year = hist.academic_year
);

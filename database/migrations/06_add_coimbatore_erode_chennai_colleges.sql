-- Migration 06: Add Coimbatore, Erode, and Chennai Regional Colleges
-- Expands the central campus directory with 38 leading institutions across western and northern Tamil Nadu.
-- Idempotent script: avoids duplicate colleges and departments.

-- 1. Insert Colleges
INSERT INTO colleges (name, code, city, state, contact_email, contact_phone, created_at)
SELECT * FROM (
    -- Erode Region
    SELECT 'Kongu Engineering College' AS name, 'KEC-PER' AS code, 'Perundurai' AS city, 'Tamil Nadu' AS state, 'placement@kongu.edu' AS contact_email, '+91 4294 226555' AS contact_phone, NOW() AS created_at
    UNION ALL SELECT 'Velalar College of Engineering and Technology', 'VCET-ERD', 'Erode', 'Tamil Nadu', 'placement@velalarengg.ac.in', '+91 424 2244201', NOW()
    UNION ALL SELECT 'Nandha Engineering College', 'NEC-ERD', 'Erode', 'Tamil Nadu', 'placement@nandhaengg.org', '+91 4294 225585', NOW()
    UNION ALL SELECT 'Nandha College of Technology', 'NCT-ERD', 'Erode', 'Tamil Nadu', 'placement@nandhatech.org', '+91 4294 223434', NOW()
    UNION ALL SELECT 'Erode Sengunthar Engineering College', 'ESEC-ERD', 'Thudupathi', 'Tamil Nadu', 'placement@erodesengunthar.ac.in', '+91 4294 232701', NOW()
    UNION ALL SELECT 'Al-Ameen Engineering College', 'AEC-ERD', 'Erode', 'Tamil Nadu', 'placement@alameen.ac.in', '+91 424 2500354', NOW()
    UNION ALL SELECT 'M.P. Nachimuthu M. Jaganathan Engineering College', 'MPNMJ-ERD', 'Erode', 'Tamil Nadu', 'placement@mpnmjec.ac.in', '+91 424 2357009', NOW()
    UNION ALL SELECT 'Surya Engineering College', 'SEC-ERD', 'Erode', 'Tamil Nadu', 'placement@surya.ac.in', '+91 424 2555018', NOW()

    -- Coimbatore Region
    UNION ALL SELECT 'Coimbatore Institute of Technology', 'CIT-CBE', 'Coimbatore', 'Tamil Nadu', 'placement@cit.edu.in', '+91 422 2574071', NOW()
    UNION ALL SELECT 'PSG Institute of Technology and Applied Research', 'PSG-ITECH', 'Coimbatore', 'Tamil Nadu', 'placement@psgitech.ac.in', '+91 422 3933666', NOW()
    UNION ALL SELECT 'Sri Krishna College of Engineering and Technology', 'SKCET-CBE', 'Coimbatore', 'Tamil Nadu', 'placement@skcet.ac.in', '+91 422 2678001', NOW()
    UNION ALL SELECT 'Sri Krishna College of Technology', 'SKCT-CBE', 'Coimbatore', 'Tamil Nadu', 'placement@skct.edu.in', '+91 422 2604567', NOW()
    UNION ALL SELECT 'Sri Ramakrishna Engineering College', 'SREC-CBE', 'Coimbatore', 'Tamil Nadu', 'placement@srec.ac.in', '+91 422 2460088', NOW()
    UNION ALL SELECT 'Sri Ramakrishna Institute of Technology', 'SRIT-CBE', 'Coimbatore', 'Tamil Nadu', 'placement@srit.org', '+91 422 2605577', NOW()
    UNION ALL SELECT 'Dr. Mahalingam College of Engineering and Technology', 'MCET-PLM', 'Pollachi', 'Tamil Nadu', 'placement@drmcet.ac.in', '+91 4259 236030', NOW()
    UNION ALL SELECT 'Karpagam College of Engineering', 'KCE-CBE', 'Coimbatore', 'Tamil Nadu', 'placement@kce.ac.in', '+91 422 2619047', NOW()
    UNION ALL SELECT 'Hindusthan College of Engineering and Technology', 'HICET-CBE', 'Coimbatore', 'Tamil Nadu', 'placement@hindusthan.net', '+91 422 2930216', NOW()
    UNION ALL SELECT 'SNS College of Technology', 'SNS-CBE', 'Coimbatore', 'Tamil Nadu', 'placement@snsct.org', '+91 422 2666264', NOW()
    UNION ALL SELECT 'Rathinam Technical Campus', 'RTC-CBE', 'Coimbatore', 'Tamil Nadu', 'placement@rathinam.in', '+91 422 4040909', NOW()
    UNION ALL SELECT 'Coimbatore Institute of Engineering and Technology', 'CIET-CBE', 'Coimbatore', 'Tamil Nadu', 'placement@cietcbe.edu.in', '+91 422 2651188', NOW()
    UNION ALL SELECT 'KPR Institute of Engineering and Technology', 'KPRIET-CBE', 'Coimbatore', 'Tamil Nadu', 'placement@kpriet.ac.in', '+91 422 2635600', NOW()
    UNION ALL SELECT 'Sri Shakthi Institute of Engineering and Technology', 'SIET-CBE', 'Coimbatore', 'Tamil Nadu', 'placement@siet.ac.in', '+91 422 2383838', NOW()

    -- Chennai Region
    UNION ALL SELECT 'SSN College of Engineering', 'SSN-CHN', 'Chennai', 'Tamil Nadu', 'placement@ssn.edu.in', '+91 44 27469700', NOW()
    UNION ALL SELECT 'Sri Venkateswara College of Engineering', 'SVCE-CHN', 'Sriperumbudur', 'Tamil Nadu', 'placement@svce.ac.in', '+91 44 27152000', NOW()
    UNION ALL SELECT 'Rajalakshmi Engineering College', 'REC-CHN', 'Chennai', 'Tamil Nadu', 'placement@rajalakshmi.edu.in', '+91 44 27156750', NOW()
    UNION ALL SELECT 'Rajalakshmi Institute of Technology', 'RIT-CHN', 'Chennai', 'Tamil Nadu', 'placement@ritchennai.edu.in', '+91 44 67181600', NOW()
    UNION ALL SELECT 'Chennai Institute of Technology', 'CIT-CHN', 'Chennai', 'Tamil Nadu', 'placement@citchennai.net', '+91 44 71119111', NOW()
    UNION ALL SELECT 'St. Joseph''s College of Engineering', 'SJCE-CHN', 'Chennai', 'Tamil Nadu', 'placement@stjosephs.ac.in', '+91 44 24501060', NOW()
    UNION ALL SELECT 'St. Joseph''s Institute of Technology', 'SJIT-CHN', 'Chennai', 'Tamil Nadu', 'placement@stjosefstechnology.ac.in', '+91 44 24503132', NOW()
    UNION ALL SELECT 'Panimalar Engineering College', 'PEC-CHN', 'Chennai', 'Tamil Nadu', 'placement@panimalar.ac.in', '+91 44 26490404', NOW()
    UNION ALL SELECT 'RMK Engineering College', 'RMK-CHN', 'Thiruvallur', 'Tamil Nadu', 'placement@rmkec.ac.in', '+91 44 67906790', NOW()
    UNION ALL SELECT 'RMD Engineering College', 'RMD-CHN', 'Thiruvallur', 'Tamil Nadu', 'placement@rmd.ac.in', '+91 44 67919191', NOW()
    UNION ALL SELECT 'Sri Sairam Engineering College', 'SAIRAM-CHN', 'Chennai', 'Tamil Nadu', 'placement@sairam.edu.in', '+91 44 22512222', NOW()
    UNION ALL SELECT 'Easwari Engineering College', 'EEC-CHN', 'Chennai', 'Tamil Nadu', 'placement@eec.srmrmp.edu.in', '+91 44 43923041', NOW()
    UNION ALL SELECT 'Meenakshi Sundararajan Engineering College', 'MSEC-CHN', 'Chennai', 'Tamil Nadu', 'placement@msec.edu.in', '+91 44 24801636', NOW()
    UNION ALL SELECT 'Loyola-ICAM College of Engineering and Technology', 'LICET-CHN', 'Chennai', 'Tamil Nadu', 'placement@licet.ac.in', '+91 44 28178490', NOW()
    UNION ALL SELECT 'Saveetha Engineering College', 'SAV-CHN', 'Chennai', 'Tamil Nadu', 'placement@saveetha.ac.in', '+91 44 66311322', NOW()
    UNION ALL SELECT 'SRM Valliammai Engineering College', 'VEC-KTR', 'Kattankulathur', 'Tamil Nadu', 'placement@valliammai.co.in', '+91 44 27454784', NOW()
) new_colleges
WHERE NOT EXISTS (
    SELECT 1 FROM colleges WHERE colleges.code = new_colleges.code OR colleges.name = new_colleges.name
);

-- 2. Populate Standard Engineering Departments for all Colleges
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
WHERE NOT EXISTS (
    SELECT 1 FROM departments WHERE college_id = c.id AND code = d.code
);

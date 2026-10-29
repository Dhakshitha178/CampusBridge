package com.campusbridge.placement.service.auth;

import com.campusbridge.placement.dto.request.ForgotPasswordRequest;
import com.campusbridge.placement.dto.request.LoginRequest;
import com.campusbridge.placement.dto.request.RecruiterRegisterRequest;
import com.campusbridge.placement.dto.request.ResetPasswordRequest;
import com.campusbridge.placement.dto.request.StudentRegisterRequest;
import com.campusbridge.placement.dto.request.TpoRegisterRequest;
import com.campusbridge.placement.dto.response.AuthResponse;
import com.campusbridge.placement.dto.response.StudentResponse;
import com.campusbridge.placement.entity.*;
import com.campusbridge.placement.entity.enums.PlacementStatus;
import com.campusbridge.placement.entity.enums.Role;
import com.campusbridge.placement.repository.*;
import com.campusbridge.placement.security.JwtUtil;
import com.campusbridge.placement.util.EmailUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final RecruiterRepository recruiterRepository;
    private final CompanyRepository companyRepository;
    private final CollegeRepository collegeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailUtil emailUtil;

    public AuthService(
            UserRepository userRepository,
            StudentRepository studentRepository,
            RecruiterRepository recruiterRepository,
            CompanyRepository companyRepository,
            CollegeRepository collegeRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            PasswordResetTokenRepository passwordResetTokenRepository,
            EmailUtil emailUtil) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.recruiterRepository = recruiterRepository;
        this.companyRepository = companyRepository;
        this.collegeRepository = collegeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailUtil = emailUtil;
    }

    public AuthResponse login(LoginRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new RuntimeException("Email address is required");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new RuntimeException("Password is required");
        }

        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new RuntimeException("Account is deactivated");
        }

        return loginUserDirect(user);
    }

    public AuthResponse loginUserDirect(User user) {
        Long profileId = null;
        Long entityRefId = null;
        Long companyId = null;
        String companyName = null;

        if (user.getRole() == Role.STUDENT) {
            Student s = studentRepository.findByUserId(user.getId()).orElse(null);
            if (s != null) {
                profileId = s.getId();
            }
        } else if (user.getRole() == Role.RECRUITER) {
            Recruiter r = recruiterRepository.findByUserId(user.getId()).orElse(null);
            if (r != null) {
                profileId = r.getId();
                if (r.getCompany() != null) {
                    companyId = r.getCompany().getId();
                    companyName = r.getCompany().getName();
                    entityRefId = companyId;
                }
            }
        }

        String token = jwtUtil.generateToken(user, profileId, entityRefId);

        Long collegeId = user.getCollege() != null ? user.getCollege().getId() : null;
        String collegeName = user.getCollege() != null ? user.getCollege().getName() : null;

        return new AuthResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                profileId,
                collegeId,
                collegeName,
                companyId,
                companyName,
                user.getApprovalStatus(),
                user.getEmailVerified(),
                user.getDesignation()
        );
    }

    @Transactional
    public StudentResponse registerStudent(StudentRegisterRequest request) {
        if (request.getEmail() == null || !request.getEmail().contains("@")) {
            throw new RuntimeException("A valid email address is required.");
        }
        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            throw new RuntimeException("Full name is required.");
        }
        if (request.getRollNumber() == null || request.getRollNumber().trim().isEmpty()) {
            throw new RuntimeException("Roll / Register number is required.");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters long.");
        }
        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }
        if (studentRepository.existsByRollNumber(request.getRollNumber().trim())) {
            throw new RuntimeException("Roll number already registered: " + request.getRollNumber());
        }

        College college = null;
        if (request.getCollegeId() != null) {
            college = collegeRepository.findById(request.getCollegeId()).orElse(null);
        }
        if (college == null) {
            college = collegeRepository.findAll().stream().findFirst().orElse(null);
        }

        User user = new User();
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.STUDENT);
        user.setFullName(request.getFullName().trim());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setCollege(college);
        user.setActive(true);
        user.setApprovalStatus("APPROVED");
        user.setEmailVerified(true);
        User savedUser = userRepository.save(user);

        Student student = new Student();
        student.setUser(savedUser);
        student.setCollege(college);
        student.setRollNumber(request.getRollNumber().trim());
        student.setDepartment(request.getDepartment() != null ? request.getDepartment() : "CSE");
        student.setAcademicYear(request.getAcademicYear() != null ? request.getAcademicYear() : "3rd Year");
        student.setGraduationYear(request.getGraduationYear() != null ? request.getGraduationYear() : 2026);
        student.setCgpa(request.getCgpa() != null ? request.getCgpa() : 8.0);
        student.setBacklogs(request.getBacklogs() != null ? request.getBacklogs() : 0);
        student.setSkills(request.getSkills() != null ? request.getSkills() : "Java, SQL");
        student.setPlacementStatus(PlacementStatus.UNPLACED);

        Student savedStudent = studentRepository.save(student);
        return new StudentResponse(savedStudent);
    }

    @Transactional
    public AuthResponse registerRecruiter(RecruiterRegisterRequest request) {
        if (request.getEmail() == null || !request.getEmail().contains("@")) {
            throw new RuntimeException("A valid official company email address is required.");
        }
        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            throw new RuntimeException("Authorized representative full name is required.");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters long.");
        }
        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        Company company = null;
        if (request.getCompanyId() != null) {
            company = companyRepository.findById(request.getCompanyId()).orElse(null);
        }
        if (company == null && request.getCompanyName() != null && !request.getCompanyName().trim().isEmpty()) {
            String cName = request.getCompanyName().trim();
            company = companyRepository.findByName(cName).orElse(null);
            if (company == null) {
                company = new Company();
                company.setName(cName);
                company.setIndustry(request.getCompanyIndustry() != null && !request.getCompanyIndustry().isBlank()
                        ? request.getCompanyIndustry()
                        : "Technology & Software");
                company.setWebsite(request.getCompanyWebsite());
                company.setVerified(true);
                company = companyRepository.save(company);
            }
        }
        if (company == null) {
            throw new RuntimeException("Company selection or corporate name is required.");
        }

        User user = new User();
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.RECRUITER);
        user.setFullName(request.getFullName().trim());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDesignation(request.getDesignation() != null && !request.getDesignation().isBlank()
                ? request.getDesignation().trim()
                : "HR Talent Acquisition Manager");
        user.setActive(true);
        user.setApprovalStatus("APPROVED");
        user.setEmailVerified(true);
        User savedUser = userRepository.save(user);

        Recruiter recruiter = new Recruiter();
        recruiter.setUser(savedUser);
        recruiter.setCompany(company);
        recruiter.setDesignation(user.getDesignation());
        Recruiter savedRecruiter = recruiterRepository.save(recruiter);

        // Send welcome email
        String subject = "CampusBridge - Welcome to Corporate Recruitment Portal";
        String body = String.format(
                "Dear %s,\n\nWelcome to CampusBridge! Your recruiter account for %s is now active and ready to use.\n\n" +
                "You can now create placement drives, schedule interviews, evaluate candidates, and manage offers.\n\n" +
                "Best regards,\nCampusBridge Placement Cell",
                savedUser.getFullName(), company.getName());
        emailUtil.sendEmail(savedUser.getEmail(), subject, body);

        String token = jwtUtil.generateToken(savedUser, savedRecruiter.getId(), company.getId());

        return new AuthResponse(
                token,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                Role.RECRUITER,
                savedRecruiter.getId(),
                null,
                null,
                company.getId(),
                company.getName(),
                savedUser.getApprovalStatus(),
                savedUser.getEmailVerified(),
                savedUser.getDesignation()
        );
    }

    @Transactional
    public AuthResponse registerTpo(TpoRegisterRequest request) {
        if (request.getEmail() == null || !request.getEmail().contains("@")) {
            throw new RuntimeException("A valid official college email address is required.");
        }
        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            throw new RuntimeException("Placement officer full name is required.");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters long.");
        }
        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        College college = null;
        if (request.getCollegeId() != null) {
            college = collegeRepository.findById(request.getCollegeId()).orElse(null);
        }
        if (college == null && request.getCollegeName() != null && !request.getCollegeName().trim().isEmpty()) {
            String colName = request.getCollegeName().trim();
            college = collegeRepository.findByName(colName).orElse(null);
            if (college == null) {
                college = new College();
                college.setName(colName);
                String code = request.getCollegeCode() != null && !request.getCollegeCode().isBlank()
                        ? request.getCollegeCode().trim().toUpperCase()
                        : "COL-" + (System.currentTimeMillis() % 100000);
                college.setCode(code);
                college.setCity("Coimbatore");
                college.setState("Tamil Nadu");
                college = collegeRepository.save(college);
            }
        }
        if (college == null) {
            college = collegeRepository.findAll().stream().findFirst().orElseThrow(() -> new RuntimeException("Institution selection or college name required."));
        }

        User user = new User();
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.TPO);
        user.setFullName(request.getFullName().trim());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDesignation(request.getDesignation() != null && !request.getDesignation().isBlank()
                ? request.getDesignation().trim()
                : "Training & Placement Officer");
        user.setCollege(college);
        user.setActive(true);
        user.setApprovalStatus("APPROVED");
        user.setEmailVerified(true);
        User savedUser = userRepository.save(user);

        // Send welcome email
        String subject = "CampusBridge - Welcome to TPO Portal";
        String body = String.format(
                "Dear %s,\n\nWelcome to CampusBridge! Your TPO account for %s is now active and ready to use.\n\n" +
                "You can now access your college placement dashboard, manage drives, and monitor student placements.\n\n" +
                "Best regards,\nCampusBridge Administration",
                savedUser.getFullName(), college.getName());
        emailUtil.sendEmail(savedUser.getEmail(), subject, body);

        String token = jwtUtil.generateToken(savedUser, null, null);

        return new AuthResponse(
                token,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                Role.TPO,
                null,
                college.getId(),
                college.getName(),
                null,
                null,
                savedUser.getApprovalStatus(),
                savedUser.getEmailVerified(),
                savedUser.getDesignation()
        );
    }

    @Transactional
    public AuthResponse verifyEmail(String email, String code) {
        if (email == null || code == null) {
            throw new RuntimeException("Email and verification code are required.");
        }
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Account not found with email: " + email));

        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(code.trim())) {
            throw new RuntimeException("Invalid verification code. Please check your email or server log.");
        }

        user.setEmailVerified(true);
        user.setApprovalStatus("APPROVED");
        user.setVerificationCode(null);
        User saved = userRepository.save(user);

        if (saved.getRole() == Role.RECRUITER) {
            recruiterRepository.findByUserId(saved.getId()).ifPresent(r -> {
                if (r.getCompany() != null && !Boolean.TRUE.equals(r.getCompany().getVerified())) {
                    r.getCompany().setVerified(true);
                    companyRepository.save(r.getCompany());
                }
            });
        }

        return loginUserDirect(saved);
    }

    @Transactional
    public AuthResponse approveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        user.setApprovalStatus("APPROVED");
        user.setEmailVerified(true);
        user.setActive(true);
        User saved = userRepository.save(user);

        if (saved.getRole() == Role.RECRUITER) {
            recruiterRepository.findByUserId(saved.getId()).ifPresent(r -> {
                if (r.getCompany() != null && !Boolean.TRUE.equals(r.getCompany().getVerified())) {
                    r.getCompany().setVerified(true);
                    companyRepository.save(r.getCompany());
                }
            });
        }

        return loginUserDirect(saved);
    }

    public AuthResponse getUserStatus(String email) {
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        return loginUserDirect(user);
    }

    public java.util.List<User> getPendingUsers() {
        return userRepository.findAll().stream()
                .filter(u -> "PENDING_APPROVAL".equalsIgnoreCase(u.getApprovalStatus()))
                .toList();
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("No account found with email: " + request.getEmail()));

        // Generate a 6-digit OTP code
        String resetCode = String.format("%06d", new java.util.Random().nextInt(999999));

        // Invalidate any existing tokens for this user
        passwordResetTokenRepository.deleteByUserId(user.getId());

        // Create new token valid for 15 minutes
        PasswordResetToken resetToken = new PasswordResetToken(
                resetCode,
                user,
                LocalDateTime.now().plusMinutes(15)
        );
        passwordResetTokenRepository.save(resetToken);

        // Send email with reset code
        String subject = "CampusBridge - Password Reset Code";
        String body = String.format(
                "Dear %s,\n\nYour password reset verification code is: %s\n\n" +
                "This code is valid for 15 minutes. If you did not request a password reset, please ignore this email.\n\n" +
                "Best regards,\nCampusBridge Placement Cell",
                user.getFullName(), resetCode);
        emailUtil.sendEmail(user.getEmail(), subject, body);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid reset code. Please request a new one."));

        if (resetToken.isUsed()) {
            throw new RuntimeException("This reset code has already been used. Please request a new one.");
        }

        if (resetToken.isExpired()) {
            throw new RuntimeException("Reset code has expired. Please request a new one.");
        }

        if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters long.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }
}

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
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new RuntimeException("Account is deactivated");
        }

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
                companyName
        );
    }

    @Transactional
    public StudentResponse registerStudent(StudentRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }
        if (studentRepository.existsByRollNumber(request.getRollNumber())) {
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
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.STUDENT);
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setCollege(college);
        User savedUser = userRepository.save(user);

        Student student = new Student();
        student.setUser(savedUser);
        student.setCollege(college);
        student.setRollNumber(request.getRollNumber());
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
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        Company company = null;
        if (request.getCompanyId() != null) {
            company = companyRepository.findById(request.getCompanyId()).orElse(null);
        }
        if (company == null && request.getCompanyName() != null) {
            company = new Company();
            company.setName(request.getCompanyName());
            company.setIndustry(request.getCompanyIndustry() != null ? request.getCompanyIndustry() : "Technology");
            company.setWebsite(request.getCompanyWebsite());
            company.setVerified(true);
            company = companyRepository.save(company);
        }
        if (company == null) {
            company = companyRepository.findAll().stream().findFirst().orElseThrow(() -> new RuntimeException("Company required"));
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.RECRUITER);
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        User savedUser = userRepository.save(user);

        Recruiter recruiter = new Recruiter();
        recruiter.setUser(savedUser);
        recruiter.setCompany(company);
        recruiter.setDesignation(request.getDesignation() != null ? request.getDesignation() : "Talent Acquisition Specialist");
        Recruiter savedRecruiter = recruiterRepository.save(recruiter);

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
                company.getName()
        );
    }

    @Transactional
    public AuthResponse registerTpo(TpoRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        College college = null;
        if (request.getCollegeId() != null) {
            college = collegeRepository.findById(request.getCollegeId()).orElse(null);
        }
        if (college == null) {
            college = collegeRepository.findAll().stream().findFirst().orElse(null);
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.TPO);
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setCollege(college);
        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(savedUser, null, null);

        Long cId = savedUser.getCollege() != null ? savedUser.getCollege().getId() : null;
        String cName = savedUser.getCollege() != null ? savedUser.getCollege().getName() : null;

        return new AuthResponse(
                token,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                Role.TPO,
                null,
                cId,
                cName,
                null,
                null
        );
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

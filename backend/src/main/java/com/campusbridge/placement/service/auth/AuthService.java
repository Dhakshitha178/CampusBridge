package com.campusbridge.placement.service.auth;

import com.campusbridge.placement.dto.request.LoginRequest;
import com.campusbridge.placement.dto.request.RecruiterRegisterRequest;
import com.campusbridge.placement.dto.request.StudentRegisterRequest;
import com.campusbridge.placement.dto.response.AuthResponse;
import com.campusbridge.placement.dto.response.StudentResponse;
import com.campusbridge.placement.entity.*;
import com.campusbridge.placement.entity.enums.PlacementStatus;
import com.campusbridge.placement.entity.enums.Role;
import com.campusbridge.placement.repository.*;
import com.campusbridge.placement.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final RecruiterRepository recruiterRepository;
    private final CompanyRepository companyRepository;
    private final CollegeRepository collegeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(
            UserRepository userRepository,
            StudentRepository studentRepository,
            RecruiterRepository recruiterRepository,
            CompanyRepository companyRepository,
            CollegeRepository collegeRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.recruiterRepository = recruiterRepository;
        this.companyRepository = companyRepository;
        this.collegeRepository = collegeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
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
}
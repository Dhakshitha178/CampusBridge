package com.campusbridge.placement.controller.auth;

import com.campusbridge.placement.dto.request.ForgotPasswordRequest;
import com.campusbridge.placement.dto.request.LoginRequest;
import com.campusbridge.placement.dto.request.RecruiterRegisterRequest;
import com.campusbridge.placement.dto.request.ResetPasswordRequest;
import com.campusbridge.placement.dto.request.StudentRegisterRequest;
import com.campusbridge.placement.dto.request.TpoRegisterRequest;
import com.campusbridge.placement.dto.response.ApiResponse;
import com.campusbridge.placement.dto.response.AuthResponse;
import com.campusbridge.placement.dto.response.StudentResponse;
import com.campusbridge.placement.entity.College;
import com.campusbridge.placement.entity.Company;
import com.campusbridge.placement.repository.CollegeRepository;
import com.campusbridge.placement.repository.CompanyRepository;
import com.campusbridge.placement.service.auth.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final CollegeRepository collegeRepository;
    private final CompanyRepository companyRepository;

    public AuthController(AuthService authService, CollegeRepository collegeRepository, CompanyRepository companyRepository) {
        this.authService = authService;
        this.collegeRepository = collegeRepository;
        this.companyRepository = companyRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/register-student")
    public ResponseEntity<ApiResponse<StudentResponse>> registerStudent(@RequestBody StudentRegisterRequest request) {
        try {
            StudentResponse response = authService.registerStudent(request);
            return ResponseEntity.ok(ApiResponse.ok("Student registration successful", response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<StudentResponse>> registerLegacy(@RequestBody StudentRegisterRequest request) {
        return registerStudent(request);
    }

    @PostMapping("/register-recruiter")
    public ResponseEntity<ApiResponse<AuthResponse>> registerRecruiter(@RequestBody RecruiterRegisterRequest request) {
        try {
            AuthResponse response = authService.registerRecruiter(request);
            return ResponseEntity.ok(ApiResponse.ok("Recruiter registration successful", response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/colleges")
    public ResponseEntity<ApiResponse<List<College>>> getColleges() {
        return ResponseEntity.ok(ApiResponse.ok(collegeRepository.findAll()));
    }

    @PostMapping("/register-tpo")
    public ResponseEntity<ApiResponse<AuthResponse>> registerTpo(@RequestBody TpoRegisterRequest request) {
        try {
            AuthResponse response = authService.registerTpo(request);
            return ResponseEntity.ok(ApiResponse.ok("TPO registration successful", response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/companies")
    public ResponseEntity<ApiResponse<List<Company>>> getCompanies() {
        return ResponseEntity.ok(ApiResponse.ok(companyRepository.findAll()));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyEmail(@RequestBody java.util.Map<String, String> request) {
        try {
            String email = request.get("email");
            String code = request.get("code");
            AuthResponse response = authService.verifyEmail(email, code);
            return ResponseEntity.ok(ApiResponse.ok("Email verified and account activated successfully!", response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/approve-user")
    public ResponseEntity<ApiResponse<AuthResponse>> approveUser(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String email) {
        try {
            if (userId == null && email != null) {
                AuthResponse status = authService.getUserStatus(email);
                userId = status.getUserId();
            }
            if (userId == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("userId or email is required"));
            }
            AuthResponse response = authService.approveUser(userId);
            return ResponseEntity.ok(ApiResponse.ok("User approved and accredited successfully!", response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<AuthResponse>> getUserStatus(@RequestParam String email) {
        try {
            AuthResponse response = authService.getUserStatus(email);
            return ResponseEntity.ok(ApiResponse.ok("User status retrieved", response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/pending-users")
    public ResponseEntity<ApiResponse<List<com.campusbridge.placement.entity.User>>> getPendingUsers() {
        return ResponseEntity.ok(ApiResponse.ok(authService.getPendingUsers()));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            authService.forgotPassword(request);
            return ResponseEntity.ok(ApiResponse.ok("Password reset code sent to your email.", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            authService.resetPassword(request);
            return ResponseEntity.ok(ApiResponse.ok("Password reset successful. You can now login with your new password.", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}

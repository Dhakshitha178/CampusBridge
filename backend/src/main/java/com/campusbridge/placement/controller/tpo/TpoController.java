package com.campusbridge.placement.controller.tpo;

import com.campusbridge.placement.dto.request.AnnouncementCreateRequest;
import com.campusbridge.placement.dto.request.DriveApprovalRequest;
import com.campusbridge.placement.dto.request.RescheduleReviewRequest;
import com.campusbridge.placement.dto.response.*;
import com.campusbridge.placement.entity.*;
import com.campusbridge.placement.entity.enums.PlacementStatus;
import com.campusbridge.placement.repository.UserRepository;
import com.campusbridge.placement.repository.WeeklyPlacementReportRepository;
import com.campusbridge.placement.service.auth.AuthService;
import com.campusbridge.placement.service.interview.InterviewSchedulingService;
import com.campusbridge.placement.service.recruiter.DriveService;
import com.campusbridge.placement.service.report.WeeklyReportService;
import com.campusbridge.placement.service.tpo.AdminService;
import com.campusbridge.placement.service.tpo.PlacementAnalyticsService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tpo")
public class TpoController {

    private final AdminService adminService;
    private final DriveService driveService;
    private final InterviewSchedulingService interviewSchedulingService;
    private final PlacementAnalyticsService analyticsService;
    private final WeeklyReportService reportService;
    private final WeeklyPlacementReportRepository reportRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    public TpoController(
            AdminService adminService,
            DriveService driveService,
            InterviewSchedulingService interviewSchedulingService,
            PlacementAnalyticsService analyticsService,
            WeeklyReportService reportService,
            WeeklyPlacementReportRepository reportRepository,
            UserRepository userRepository,
            AuthService authService) {
        this.adminService = adminService;
        this.driveService = driveService;
        this.interviewSchedulingService = interviewSchedulingService;
        this.analyticsService = analyticsService;
        this.reportService = reportService;
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    private User getCurrentTpo() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElse(null);
    }

    private Long resolveEffectiveCollegeId(Long requestedCollegeId) {
        User tpo = getCurrentTpo();
        if (tpo != null && tpo.getCollege() != null) {
            if (requestedCollegeId != null && !requestedCollegeId.equals(tpo.getCollege().getId())) {
                throw new RuntimeException("Access Denied: You are not authorized to access records from another institution.");
            }
            return tpo.getCollege().getId();
        }
        return requestedCollegeId;
    }

    @GetMapping("/pending-users")
    public ResponseEntity<ApiResponse<List<User>>> getPendingUsers() {
        getCurrentTpo();
        return ResponseEntity.ok(ApiResponse.ok(authService.getPendingUsers()));
    }

    @PostMapping("/users/{userId}/approve")
    public ResponseEntity<ApiResponse<AuthResponse>> approveUser(@PathVariable Long userId) {
        getCurrentTpo();
        return ResponseEntity.ok(ApiResponse.ok("User account approved successfully", authService.approveUser(userId)));
    }

    @GetMapping("/dashboard-stats")
    public ResponseEntity<ApiResponse<TpoDashboardStatsDto>> getDashboardStats(
            @RequestParam(required = false) Long collegeId) {
        Long effectiveCollegeId = resolveEffectiveCollegeId(collegeId);
        return ResponseEntity.ok(ApiResponse.ok(adminService.getDashboardStats(effectiveCollegeId)));
    }

    @GetMapping("/students")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> searchStudents(
            @RequestParam(required = false) Long collegeId,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Double minCgpa,
            @RequestParam(required = false) Integer maxBacklogs,
            @RequestParam(required = false) PlacementStatus status) {
        Long effectiveCollegeId = resolveEffectiveCollegeId(collegeId);
        return ResponseEntity.ok(ApiResponse.ok(adminService.searchStudents(effectiveCollegeId, department, minCgpa, maxBacklogs, status)));
    }

    @GetMapping("/companies")
    public ResponseEntity<ApiResponse<List<Company>>> getCompanies() {
        return ResponseEntity.ok(ApiResponse.ok(adminService.getAllCompanies()));
    }

    @PostMapping("/companies/{companyId}/verify")
    public ResponseEntity<ApiResponse<Company>> verifyCompany(
            @PathVariable Long companyId,
            @RequestParam boolean verified) {
        return ResponseEntity.ok(ApiResponse.ok("Company status updated", adminService.verifyCompany(companyId, verified)));
    }

    @GetMapping("/drives")
    public ResponseEntity<ApiResponse<List<DriveResponse>>> getAllDrives() {
        return ResponseEntity.ok(ApiResponse.ok(driveService.getAllDrivesForTpo()));
    }

    @PostMapping("/drives/{driveId}/review")
    public ResponseEntity<ApiResponse<DriveResponse>> reviewDrive(
            @PathVariable Long driveId,
            @RequestBody DriveApprovalRequest request,
            @RequestParam(defaultValue = "TPO Office") String reviewerName) {
        return ResponseEntity.ok(ApiResponse.ok("Drive review updated",
                driveService.reviewDrive(driveId, request, reviewerName)));
    }

    @GetMapping("/live-monitor")
    public ResponseEntity<ApiResponse<List<LiveDriveMonitorDto>>> getLiveDriveMonitor(
            @RequestParam(required = false) Long collegeId) {
        Long effectiveCollegeId = resolveEffectiveCollegeId(collegeId);
        return ResponseEntity.ok(ApiResponse.ok(adminService.getLiveDriveMonitor(effectiveCollegeId)));
    }

    @GetMapping("/calendar")
    public ResponseEntity<ApiResponse<List<InterviewResponseDto>>> getCentralCalendar(
            @RequestParam(required = false) Long collegeId) {
        Long effectiveCollegeId = resolveEffectiveCollegeId(collegeId);
        return ResponseEntity.ok(ApiResponse.ok(interviewSchedulingService.getCentralCalendarForTpo(effectiveCollegeId)));
    }

    @GetMapping("/reschedule-requests")
    public ResponseEntity<ApiResponse<List<RescheduleRequest>>> getRescheduleRequests(
            @RequestParam(required = false) Long collegeId) {
        Long effectiveCollegeId = resolveEffectiveCollegeId(collegeId);
        return ResponseEntity.ok(ApiResponse.ok(interviewSchedulingService.getPendingRescheduleRequests(effectiveCollegeId)));
    }

    @PostMapping("/reschedule-requests/{requestId}/review")
    public ResponseEntity<ApiResponse<RescheduleRequest>> reviewRescheduleRequest(
            @PathVariable Long requestId,
            @RequestBody RescheduleReviewRequest request,
            @RequestParam(defaultValue = "Placement Coordinator") String reviewerName) {
        getCurrentTpo();
        return ResponseEntity.ok(ApiResponse.ok("Rescheduling decision recorded and calendar updated",
                interviewSchedulingService.reviewRescheduleRequest(requestId, request, reviewerName)));
    }

    @GetMapping("/announcements")
    public ResponseEntity<ApiResponse<List<Announcement>>> getAnnouncements(
            @RequestParam(required = false) Long collegeId) {
        Long effectiveCollegeId = resolveEffectiveCollegeId(collegeId);
        return ResponseEntity.ok(ApiResponse.ok(adminService.getAnnouncements(effectiveCollegeId)));
    }

    @PostMapping("/announcements")
    public ResponseEntity<ApiResponse<Announcement>> createAnnouncement(
            @RequestParam(required = false) Long collegeId,
            @RequestBody AnnouncementCreateRequest request,
            @RequestParam(defaultValue = "TPO Office") String postedBy) {
        Long effectiveCollegeId = resolveEffectiveCollegeId(collegeId);
        return ResponseEntity.ok(ApiResponse.ok("Announcement published",
                adminService.createAnnouncement(effectiveCollegeId, request, postedBy)));
    }

    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse<PlacementAnalyticsDto>> getAnalytics(
            @RequestParam(required = false) Long collegeId) {
        Long effectiveCollegeId = resolveEffectiveCollegeId(collegeId);
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getAnalytics(effectiveCollegeId)));
    }

    @GetMapping("/reports")
    public ResponseEntity<ApiResponse<List<WeeklyPlacementReport>>> getReports(
            @RequestParam(required = false) Long collegeId) {
        Long effectiveCollegeId = resolveEffectiveCollegeId(collegeId);
        return ResponseEntity.ok(ApiResponse.ok(
                effectiveCollegeId != null ? reportRepository.findByCollegeIdOrderByGeneratedAtDesc(effectiveCollegeId) : reportRepository.findAll()));
    }

    @PostMapping("/reports/generate")
    public ResponseEntity<ApiResponse<WeeklyPlacementReport>> generateReport(
            @RequestParam(required = false) Long collegeId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "Placement Director") String generatedBy) {
        Long effectiveCollegeId = resolveEffectiveCollegeId(collegeId);
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusDays(7);
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();
        return ResponseEntity.ok(ApiResponse.ok("Report snapshot generated successfully",
                reportService.generateReport(effectiveCollegeId, start, end, generatedBy)));
    }

    @GetMapping("/reports/{reportId}/pdf")
    public ResponseEntity<byte[]> downloadPdfReport(@PathVariable Long reportId) {
        try {
            byte[] pdfBytes = reportService.generatePdfReport(reportId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=CampusBridge_Weekly_Report_" + reportId + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/reports/{reportId}/excel")
    public ResponseEntity<byte[]> downloadExcelReport(@PathVariable Long reportId) {
        try {
            byte[] excelBytes = reportService.generateExcelReport(reportId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=CampusBridge_Placement_Report_" + reportId + ".xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excelBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/reports/export/excel")
    public ResponseEntity<byte[]> exportMasterPlacementExcel(
            @RequestParam(required = false) Long collegeId) {
        try {
            Long effectiveCollegeId = resolveEffectiveCollegeId(collegeId);
            byte[] excelBytes = reportService.generateMasterPlacementExcel(effectiveCollegeId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=CampusBridge_Placement_Master_" + java.time.LocalDate.now() + ".xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excelBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

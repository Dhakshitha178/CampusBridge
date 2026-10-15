package com.campusbridge.placement.controller.tpo;

import com.campusbridge.placement.dto.request.AnnouncementCreateRequest;
import com.campusbridge.placement.dto.request.DriveApprovalRequest;
import com.campusbridge.placement.dto.request.RescheduleReviewRequest;
import com.campusbridge.placement.dto.response.*;
import com.campusbridge.placement.entity.*;
import com.campusbridge.placement.entity.enums.PlacementStatus;
import com.campusbridge.placement.repository.WeeklyPlacementReportRepository;
import com.campusbridge.placement.service.interview.InterviewSchedulingService;
import com.campusbridge.placement.service.recruiter.DriveService;
import com.campusbridge.placement.service.report.WeeklyReportService;
import com.campusbridge.placement.service.tpo.AdminService;
import com.campusbridge.placement.service.tpo.PlacementAnalyticsService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    public TpoController(
            AdminService adminService,
            DriveService driveService,
            InterviewSchedulingService interviewSchedulingService,
            PlacementAnalyticsService analyticsService,
            WeeklyReportService reportService,
            WeeklyPlacementReportRepository reportRepository) {
        this.adminService = adminService;
        this.driveService = driveService;
        this.interviewSchedulingService = interviewSchedulingService;
        this.analyticsService = analyticsService;
        this.reportService = reportService;
        this.reportRepository = reportRepository;
    }

    @GetMapping("/dashboard-stats")
    public ResponseEntity<ApiResponse<TpoDashboardStatsDto>> getDashboardStats(
            @RequestParam(required = false) Long collegeId) {
        return ResponseEntity.ok(ApiResponse.ok(adminService.getDashboardStats(collegeId)));
    }

    @GetMapping("/students")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> searchStudents(
            @RequestParam(required = false) Long collegeId,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Double minCgpa,
            @RequestParam(required = false) Integer maxBacklogs,
            @RequestParam(required = false) PlacementStatus status) {
        return ResponseEntity.ok(ApiResponse.ok(adminService.searchStudents(collegeId, department, minCgpa, maxBacklogs, status)));
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
        return ResponseEntity.ok(ApiResponse.ok(adminService.getLiveDriveMonitor(collegeId)));
    }

    @GetMapping("/calendar")
    public ResponseEntity<ApiResponse<List<InterviewResponseDto>>> getCentralCalendar(
            @RequestParam(required = false) Long collegeId) {
        return ResponseEntity.ok(ApiResponse.ok(interviewSchedulingService.getCentralCalendarForTpo(collegeId)));
    }

    @GetMapping("/reschedule-requests")
    public ResponseEntity<ApiResponse<List<RescheduleRequest>>> getRescheduleRequests(
            @RequestParam(required = false) Long collegeId) {
        return ResponseEntity.ok(ApiResponse.ok(interviewSchedulingService.getPendingRescheduleRequests(collegeId)));
    }

    @PostMapping("/reschedule-requests/{requestId}/review")
    public ResponseEntity<ApiResponse<RescheduleRequest>> reviewRescheduleRequest(
            @PathVariable Long requestId,
            @RequestBody RescheduleReviewRequest request,
            @RequestParam(defaultValue = "Placement Coordinator") String reviewerName) {
        return ResponseEntity.ok(ApiResponse.ok("Rescheduling decision recorded and calendar updated",
                interviewSchedulingService.reviewRescheduleRequest(requestId, request, reviewerName)));
    }

    @GetMapping("/announcements")
    public ResponseEntity<ApiResponse<List<Announcement>>> getAnnouncements(
            @RequestParam(required = false) Long collegeId) {
        return ResponseEntity.ok(ApiResponse.ok(adminService.getAnnouncements(collegeId)));
    }

    @PostMapping("/announcements")
    public ResponseEntity<ApiResponse<Announcement>> createAnnouncement(
            @RequestParam(required = false) Long collegeId,
            @RequestBody AnnouncementCreateRequest request,
            @RequestParam(defaultValue = "TPO Office") String postedBy) {
        return ResponseEntity.ok(ApiResponse.ok("Announcement published",
                adminService.createAnnouncement(collegeId, request, postedBy)));
    }

    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse<PlacementAnalyticsDto>> getAnalytics(
            @RequestParam(required = false) Long collegeId) {
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getAnalytics(collegeId)));
    }

    @GetMapping("/reports")
    public ResponseEntity<ApiResponse<List<WeeklyPlacementReport>>> getReports(
            @RequestParam(required = false) Long collegeId) {
        return ResponseEntity.ok(ApiResponse.ok(
                collegeId != null ? reportRepository.findByCollegeIdOrderByGeneratedAtDesc(collegeId) : reportRepository.findAll()));
    }

    @PostMapping("/reports/generate")
    public ResponseEntity<ApiResponse<WeeklyPlacementReport>> generateReport(
            @RequestParam(required = false) Long collegeId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "Placement Director") String generatedBy) {
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusDays(7);
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();
        return ResponseEntity.ok(ApiResponse.ok("Report snapshot generated successfully",
                reportService.generateReport(collegeId, start, end, generatedBy)));
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
            byte[] excelBytes = reportService.generateMasterPlacementExcel(collegeId);
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

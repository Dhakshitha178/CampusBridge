package com.campusbridge.placement.controller.student;

import com.campusbridge.placement.dto.request.ApplicationRequest;
import com.campusbridge.placement.dto.request.RescheduleSubmitRequest;
import com.campusbridge.placement.dto.request.StudentProfileUpdateRequest;
import com.campusbridge.placement.dto.response.*;
import com.campusbridge.placement.entity.CompanyPlacementHistory;
import com.campusbridge.placement.entity.Offer;
import com.campusbridge.placement.entity.RescheduleRequest;
import com.campusbridge.placement.entity.enums.OfferStatus;
import com.campusbridge.placement.service.interview.InterviewSchedulingService;
import com.campusbridge.placement.service.offer.OfferService;
import com.campusbridge.placement.service.student.ApplicationService;
import com.campusbridge.placement.service.student.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final StudentService studentService;
    private final ApplicationService applicationService;
    private final InterviewSchedulingService interviewSchedulingService;
    private final OfferService offerService;

    public StudentController(
            StudentService studentService,
            ApplicationService applicationService,
            InterviewSchedulingService interviewSchedulingService,
            OfferService offerService) {
        this.studentService = studentService;
        this.applicationService = applicationService;
        this.interviewSchedulingService = interviewSchedulingService;
        this.offerService = offerService;
    }

    @GetMapping("/{studentId}/dashboard-stats")
    public ResponseEntity<ApiResponse<StudentDashboardStatsDto>> getDashboardStats(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.ok(studentService.getDashboardStats(studentId)));
    }

    @GetMapping("/{studentId}/profile")
    public ResponseEntity<ApiResponse<StudentResponse>> getProfile(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.ok(studentService.getProfile(studentId)));
    }

    @PutMapping("/{studentId}/profile")
    public ResponseEntity<ApiResponse<StudentResponse>> updateProfile(
            @PathVariable Long studentId,
            @RequestBody StudentProfileUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully", studentService.updateProfile(studentId, request)));
    }

    @GetMapping("/{studentId}/applications")
    public ResponseEntity<ApiResponse<List<ApplicationStatusResponse>>> getApplications(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.ok(applicationService.getStudentApplications(studentId)));
    }

    @PostMapping("/{studentId}/apply")
    public ResponseEntity<ApiResponse<ApplicationStatusResponse>> apply(
            @PathVariable Long studentId,
            @RequestBody ApplicationRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Application submitted successfully", applicationService.apply(studentId, request)));
    }

    @PostMapping("/{studentId}/withdraw/{applicationId}")
    public ResponseEntity<ApiResponse<ApplicationStatusResponse>> withdraw(
            @PathVariable Long studentId,
            @PathVariable Long applicationId) {
        return ResponseEntity.ok(ApiResponse.ok("Application withdrawn", applicationService.withdraw(applicationId, studentId)));
    }

    @GetMapping("/{studentId}/tracker")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getPlacementTracker(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.ok(studentService.getMultiCompanyTracker(studentId)));
    }

    @GetMapping("/{studentId}/calendar")
    public ResponseEntity<ApiResponse<List<InterviewResponseDto>>> getUnifiedCalendar(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.ok(interviewSchedulingService.getStudentCalendar(studentId)));
    }

    @PostMapping("/{studentId}/interviews/{interviewId}/reschedule")
    public ResponseEntity<ApiResponse<RescheduleRequest>> requestReschedule(
            @PathVariable Long studentId,
            @PathVariable Long interviewId,
            @RequestBody RescheduleSubmitRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Rescheduling request submitted to TPO",
                interviewSchedulingService.submitRescheduleRequest(interviewId, studentId, request)));
    }

    @GetMapping("/{studentId}/offers")
    public ResponseEntity<ApiResponse<List<Offer>>> getOffers(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.ok(offerService.getStudentOffers(studentId)));
    }

    @PostMapping("/{studentId}/offers/{offerId}/respond")
    public ResponseEntity<ApiResponse<Offer>> respondToOffer(
            @PathVariable Long studentId,
            @PathVariable Long offerId,
            @RequestParam OfferStatus status) {
        return ResponseEntity.ok(ApiResponse.ok("Offer status updated",
                offerService.updateOfferStatus(offerId, studentId, status)));
    }

    @GetMapping("/company-history")
    public ResponseEntity<ApiResponse<List<CompanyPlacementHistory>>> getCompanyHistory(@RequestParam(required = false) Long collegeId) {
        return ResponseEntity.ok(ApiResponse.ok(studentService.getCompanyPlacementHistory(collegeId)));
    }

    @GetMapping("/inter-college-history")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getInterCollegeHistory() {
        return ResponseEntity.ok(ApiResponse.ok(studentService.getInterCollegeAggregatedHistory()));
    }
}
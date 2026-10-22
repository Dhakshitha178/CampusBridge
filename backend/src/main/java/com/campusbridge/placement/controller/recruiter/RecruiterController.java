package com.campusbridge.placement.controller.recruiter;

import com.campusbridge.placement.dto.request.*;
import com.campusbridge.placement.dto.response.*;
import com.campusbridge.placement.entity.*;
import com.campusbridge.placement.entity.enums.ApplicationStatus;
import com.campusbridge.placement.repository.RecruiterRepository;
import com.campusbridge.placement.service.interview.InterviewSchedulingService;
import com.campusbridge.placement.service.interview.SchedulingConflictService;
import com.campusbridge.placement.service.offer.OfferService;
import com.campusbridge.placement.service.recruiter.CollegeRecommendationService;
import com.campusbridge.placement.service.recruiter.CompanyService;
import com.campusbridge.placement.service.recruiter.DriveService;
import com.campusbridge.placement.service.student.ApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/recruiter")
public class RecruiterController {

    private final RecruiterRepository recruiterRepository;
    private final CompanyService companyService;
    private final DriveService driveService;
    private final ApplicationService applicationService;
    private final InterviewSchedulingService interviewSchedulingService;
    private final OfferService offerService;
    private final CollegeRecommendationService recommendationService;
    private final SchedulingConflictService conflictService;

    public RecruiterController(
            RecruiterRepository recruiterRepository,
            CompanyService companyService,
            DriveService driveService,
            ApplicationService applicationService,
            InterviewSchedulingService interviewSchedulingService,
            OfferService offerService,
            CollegeRecommendationService recommendationService,
            SchedulingConflictService conflictService) {
        this.recruiterRepository = recruiterRepository;
        this.companyService = companyService;
        this.driveService = driveService;
        this.applicationService = applicationService;
        this.interviewSchedulingService = interviewSchedulingService;
        this.offerService = offerService;
        this.recommendationService = recommendationService;
        this.conflictService = conflictService;
    }

    // ──────────────────────────────────────────────────────────
    // Helper: resolve the logged-in recruiter from JWT/SecurityContext
    // ──────────────────────────────────────────────────────────
    private Recruiter getCurrentRecruiter() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return recruiterRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Recruiter profile not found for: " + email));
    }

    // ──────────────────────────────────────────────────────────
    // JWT-based endpoints (no recruiterId in URL)
    // These MUST be declared BEFORE the /{recruiterId}/... endpoints
    // so Spring matches literal paths before the path-variable pattern.
    // ──────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<RecruiterDashboardStatsDto>> getDashboard() {
        Recruiter recruiter = getCurrentRecruiter();
        return ResponseEntity.ok(ApiResponse.ok(companyService.getDashboardStats(recruiter.getCompany().getId())));
    }

    @GetMapping("/company")
    public ResponseEntity<ApiResponse<Company>> getMyCompany() {
        Recruiter recruiter = getCurrentRecruiter();
        return ResponseEntity.ok(ApiResponse.ok(recruiter.getCompany()));
    }

    @PutMapping("/company")
    public ResponseEntity<ApiResponse<Company>> updateMyCompany(@RequestBody Company updated) {
        Recruiter recruiter = getCurrentRecruiter();
        return ResponseEntity.ok(ApiResponse.ok("Company profile updated",
                companyService.updateCompany(recruiter.getCompany().getId(), updated)));
    }

    @GetMapping("/drives")
    public ResponseEntity<ApiResponse<List<DriveResponse>>> getMyDrives() {
        Recruiter recruiter = getCurrentRecruiter();
        return ResponseEntity.ok(ApiResponse.ok(driveService.getDrivesForCompany(recruiter.getCompany().getId())));
    }

    @PostMapping("/drives")
    public ResponseEntity<ApiResponse<DriveResponse>> createMyDrive(@RequestBody DriveCreateRequest request) {
        Recruiter recruiter = getCurrentRecruiter();
        return ResponseEntity.ok(ApiResponse.ok("Drive submitted for TPO approval",
                driveService.createDrive(recruiter, request)));
    }

    @GetMapping("/interviews")
    public ResponseEntity<ApiResponse<List<InterviewResponseDto>>> getMyInterviews() {
        Recruiter recruiter = getCurrentRecruiter();
        return ResponseEntity.ok(ApiResponse.ok(interviewSchedulingService.getCompanyCalendar(recruiter.getCompany().getId())));
    }

    @GetMapping("/interviewers")
    public ResponseEntity<ApiResponse<List<Interviewer>>> getMyInterviewers() {
        Recruiter recruiter = getCurrentRecruiter();
        return ResponseEntity.ok(ApiResponse.ok(companyService.getInterviewers(recruiter.getCompany().getId())));
    }

    @PostMapping("/interviewers")
    public ResponseEntity<ApiResponse<Interviewer>> addMyInterviewer(@RequestBody Interviewer interviewer) {
        Recruiter recruiter = getCurrentRecruiter();
        return ResponseEntity.ok(ApiResponse.ok("Interviewer added",
                companyService.addInterviewer(recruiter.getCompany().getId(), interviewer)));
    }

    @GetMapping("/panels")
    public ResponseEntity<ApiResponse<List<InterviewPanel>>> getMyPanels() {
        Recruiter recruiter = getCurrentRecruiter();
        return ResponseEntity.ok(ApiResponse.ok(companyService.getPanels(recruiter.getCompany().getId())));
    }

    @PostMapping("/panels")
    public ResponseEntity<ApiResponse<InterviewPanel>> createMyPanel(
            @RequestParam String panelName,
            @RequestParam(required = false) String description,
            @RequestBody(required = false) List<Long> interviewerIds) {
        Recruiter recruiter = getCurrentRecruiter();
        return ResponseEntity.ok(ApiResponse.ok("Panel created",
                companyService.createPanel(recruiter.getCompany().getId(), panelName, description, interviewerIds)));
    }

    @GetMapping("/offers")
    public ResponseEntity<ApiResponse<List<Offer>>> getMyOffers() {
        Recruiter recruiter = getCurrentRecruiter();
        return ResponseEntity.ok(ApiResponse.ok(offerService.getCompanyOffers(recruiter.getCompany().getId())));
    }

    @GetMapping("/recommendations/colleges")
    public ResponseEntity<ApiResponse<List<CollegeRecommendationDto>>> getMyRecommendations() {
        Recruiter recruiter = getCurrentRecruiter();
        return ResponseEntity.ok(ApiResponse.ok(recommendationService.getRecommendationsForCompany(recruiter.getCompany().getId())));
    }

    // ──────────────────────────────────────────────────────────
    // Existing ID-based endpoints (kept for backward compatibility)
    // ──────────────────────────────────────────────────────────

    @GetMapping("/{recruiterId}/dashboard-stats")
    public ResponseEntity<ApiResponse<RecruiterDashboardStatsDto>> getDashboardStats(@PathVariable Long recruiterId) {
        Recruiter recruiter = recruiterRepository.findById(recruiterId)
                .orElseThrow(() -> new RuntimeException("Recruiter not found: " + recruiterId));
        return ResponseEntity.ok(ApiResponse.ok(companyService.getDashboardStats(recruiter.getCompany().getId())));
    }

    @GetMapping("/{recruiterId}/company")
    public ResponseEntity<ApiResponse<Company>> getCompany(@PathVariable Long recruiterId) {
        Recruiter recruiter = recruiterRepository.findById(recruiterId)
                .orElseThrow(() -> new RuntimeException("Recruiter not found: " + recruiterId));
        return ResponseEntity.ok(ApiResponse.ok(recruiter.getCompany()));
    }

    @PutMapping("/{recruiterId}/company")
    public ResponseEntity<ApiResponse<Company>> updateCompany(
            @PathVariable Long recruiterId,
            @RequestBody Company updated) {
        Recruiter recruiter = recruiterRepository.findById(recruiterId)
                .orElseThrow(() -> new RuntimeException("Recruiter not found: " + recruiterId));
        return ResponseEntity.ok(ApiResponse.ok("Company profile updated",
                companyService.updateCompany(recruiter.getCompany().getId(), updated)));
    }

    @GetMapping("/{recruiterId}/drives")
    public ResponseEntity<ApiResponse<List<DriveResponse>>> getDrives(@PathVariable Long recruiterId) {
        Recruiter recruiter = recruiterRepository.findById(recruiterId)
                .orElseThrow(() -> new RuntimeException("Recruiter not found: " + recruiterId));
        return ResponseEntity.ok(ApiResponse.ok(driveService.getDrivesForCompany(recruiter.getCompany().getId())));
    }

    @PostMapping("/{recruiterId}/drives")
    public ResponseEntity<ApiResponse<DriveResponse>> createDrive(
            @PathVariable Long recruiterId,
            @RequestBody DriveCreateRequest request) {
        Recruiter recruiter = recruiterRepository.findById(recruiterId)
                .orElseThrow(() -> new RuntimeException("Recruiter not found: " + recruiterId));
        return ResponseEntity.ok(ApiResponse.ok("Drive submitted for TPO approval",
                driveService.createDrive(recruiter, request)));
    }

    @GetMapping("/drives/{driveId}/applicants")
    public ResponseEntity<ApiResponse<List<ApplicationStatusResponse>>> getDriveApplicants(@PathVariable Long driveId) {
        return ResponseEntity.ok(ApiResponse.ok(applicationService.getDriveApplications(driveId)));
    }

    @PostMapping("/applications/{applicationId}/stage")
    public ResponseEntity<ApiResponse<ApplicationStatusResponse>> updateApplicationStage(
            @PathVariable Long applicationId,
            @RequestParam(required = false) Long nextStageId,
            @RequestParam ApplicationStatus status,
            @RequestParam(required = false) String remarks) {
        return ResponseEntity.ok(ApiResponse.ok("Application stage updated",
                applicationService.updateStage(applicationId, nextStageId, status, remarks, "Recruiter")));
    }

    @PostMapping("/drives/{driveId}/bulk-results")
    public ResponseEntity<ApiResponse<List<ApplicationStatusResponse>>> bulkUploadResults(
            @PathVariable Long driveId,
            @RequestBody List<BulkResultRowDto> rows) {
        return ResponseEntity.ok(ApiResponse.ok("Bulk results processed successfully",
                applicationService.bulkProcessResults(driveId, rows, "Recruiter Bulk Upload")));
    }

    @PostMapping("/interviews/schedule")
    public ResponseEntity<ApiResponse<List<InterviewResponseDto>>> scheduleInterviews(
            @RequestBody InterviewScheduleRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Interviews scheduled successfully",
                interviewSchedulingService.scheduleInterviews(request)));
    }

    @PostMapping("/interviews/check-conflicts")
    public ResponseEntity<ApiResponse<ConflictReportDto>> checkConflicts(
            @RequestBody java.util.Map<String, Object> checkData) {
        Long studentId = checkData.get("studentId") != null ? Long.valueOf(checkData.get("studentId").toString()) : null;
        LocalDate interviewDate = checkData.get("interviewDate") != null ? LocalDate.parse(checkData.get("interviewDate").toString()) : null;
        LocalTime startTime = checkData.get("startTime") != null ? LocalTime.parse(checkData.get("startTime").toString()) : null;
        LocalTime endTime = checkData.get("endTime") != null ? LocalTime.parse(checkData.get("endTime").toString()) : null;
        String mode = checkData.get("mode") != null ? checkData.get("mode").toString() : "ONLINE";
        String venue = checkData.get("venue") != null ? checkData.get("venue").toString() : null;
        return ResponseEntity.ok(ApiResponse.ok(
                conflictService.checkConflicts(studentId, interviewDate, startTime, endTime, mode, venue, null, null)));
    }

    @GetMapping("/{companyId}/calendar")
    public ResponseEntity<ApiResponse<List<InterviewResponseDto>>> getCompanyCalendar(@PathVariable Long companyId) {
        return ResponseEntity.ok(ApiResponse.ok(interviewSchedulingService.getCompanyCalendar(companyId)));
    }

    @PostMapping("/interviews/{interviewId}/evaluate")
    public ResponseEntity<ApiResponse<EvaluationResponseDto>> recordEvaluation(
            @PathVariable Long interviewId,
            @RequestBody EvaluationRequest request,
            @RequestParam(defaultValue = "Technical Interviewer") String evaluatorName) {
        InterviewEvaluation saved = interviewSchedulingService.recordEvaluation(interviewId, request, evaluatorName);
        return ResponseEntity.ok(ApiResponse.ok("Interview evaluation recorded",
                new EvaluationResponseDto(saved)));
    }

    @GetMapping("/interviews/{interviewId}/evaluate")
    public ResponseEntity<ApiResponse<EvaluationResponseDto>> getEvaluation(@PathVariable Long interviewId) {
        return interviewSchedulingService.getEvaluationByInterviewId(interviewId)
                .map(eval -> ResponseEntity.ok(ApiResponse.ok(new EvaluationResponseDto(eval))))
                .orElse(ResponseEntity.ok(ApiResponse.ok(null)));
    }

    @GetMapping("/{companyId}/interviewers")
    public ResponseEntity<ApiResponse<List<Interviewer>>> getInterviewers(@PathVariable Long companyId) {
        return ResponseEntity.ok(ApiResponse.ok(companyService.getInterviewers(companyId)));
    }

    @PostMapping("/{companyId}/interviewers")
    public ResponseEntity<ApiResponse<Interviewer>> addInterviewer(
            @PathVariable Long companyId,
            @RequestBody Interviewer interviewer) {
        return ResponseEntity.ok(ApiResponse.ok("Interviewer added", companyService.addInterviewer(companyId, interviewer)));
    }

    @GetMapping("/{companyId}/panels")
    public ResponseEntity<ApiResponse<List<InterviewPanel>>> getPanels(@PathVariable Long companyId) {
        return ResponseEntity.ok(ApiResponse.ok(companyService.getPanels(companyId)));
    }

    @PostMapping("/{companyId}/panels")
    public ResponseEntity<ApiResponse<InterviewPanel>> createPanel(
            @PathVariable Long companyId,
            @RequestParam String panelName,
            @RequestParam(required = false) String description,
            @RequestBody(required = false) List<Long> interviewerIds) {
        return ResponseEntity.ok(ApiResponse.ok("Panel created",
                companyService.createPanel(companyId, panelName, description, interviewerIds)));
    }

    @PostMapping("/offers")
    public ResponseEntity<ApiResponse<Offer>> createOffer(@RequestBody OfferCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Offer created and released to student",
                offerService.createOffer(request)));
    }

    @GetMapping("/{companyId}/offers")
    public ResponseEntity<ApiResponse<List<Offer>>> getOffers(@PathVariable Long companyId) {
        return ResponseEntity.ok(ApiResponse.ok(offerService.getCompanyOffers(companyId)));
    }

    @GetMapping("/{companyId}/recommendations")
    public ResponseEntity<ApiResponse<List<CollegeRecommendationDto>>> getCollegeRecommendations(@PathVariable Long companyId) {
        return ResponseEntity.ok(ApiResponse.ok(recommendationService.getRecommendationsForCompany(companyId)));
    }
}

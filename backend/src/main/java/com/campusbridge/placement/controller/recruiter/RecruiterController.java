package com.campusbridge.placement.controller.recruiter;

import com.campusbridge.placement.dto.request.*;
import com.campusbridge.placement.dto.response.*;
import com.campusbridge.placement.entity.*;
import com.campusbridge.placement.entity.enums.ApplicationStatus;
import com.campusbridge.placement.repository.RecruiterRepository;
import com.campusbridge.placement.service.interview.InterviewSchedulingService;
import com.campusbridge.placement.service.offer.OfferService;
import com.campusbridge.placement.service.recruiter.CollegeRecommendationService;
import com.campusbridge.placement.service.recruiter.CompanyService;
import com.campusbridge.placement.service.recruiter.DriveService;
import com.campusbridge.placement.service.student.ApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    public RecruiterController(
            RecruiterRepository recruiterRepository,
            CompanyService companyService,
            DriveService driveService,
            ApplicationService applicationService,
            InterviewSchedulingService interviewSchedulingService,
            OfferService offerService,
            CollegeRecommendationService recommendationService) {
        this.recruiterRepository = recruiterRepository;
        this.companyService = companyService;
        this.driveService = driveService;
        this.applicationService = applicationService;
        this.interviewSchedulingService = interviewSchedulingService;
        this.offerService = offerService;
        this.recommendationService = recommendationService;
    }

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

    @GetMapping("/{companyId}/calendar")
    public ResponseEntity<ApiResponse<List<InterviewResponseDto>>> getCompanyCalendar(@PathVariable Long companyId) {
        return ResponseEntity.ok(ApiResponse.ok(interviewSchedulingService.getCompanyCalendar(companyId)));
    }

    @PostMapping("/interviews/{interviewId}/evaluate")
    public ResponseEntity<ApiResponse<InterviewEvaluation>> recordEvaluation(
            @PathVariable Long interviewId,
            @RequestBody EvaluationRequest request,
            @RequestParam(defaultValue = "Technical Interviewer") String evaluatorName) {
        return ResponseEntity.ok(ApiResponse.ok("Interview evaluation recorded",
                interviewSchedulingService.recordEvaluation(interviewId, request, evaluatorName)));
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

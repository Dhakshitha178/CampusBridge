package com.campusbridge.placement.service.interview;

import com.campusbridge.placement.dto.request.EvaluationRequest;
import com.campusbridge.placement.dto.request.InterviewScheduleRequest;
import com.campusbridge.placement.dto.request.RescheduleReviewRequest;
import com.campusbridge.placement.dto.request.RescheduleSubmitRequest;
import com.campusbridge.placement.dto.response.ConflictReportDto;
import com.campusbridge.placement.dto.response.InterviewResponseDto;
import com.campusbridge.placement.entity.*;
import com.campusbridge.placement.entity.enums.ApplicationStatus;
import com.campusbridge.placement.entity.enums.EvaluationDecision;
import com.campusbridge.placement.entity.enums.InterviewStatus;
import com.campusbridge.placement.entity.enums.RescheduleStatus;
import com.campusbridge.placement.repository.*;
import com.campusbridge.placement.service.notification.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InterviewSchedulingService {

    private final InterviewRepository interviewRepository;
    private final PlacementDriveRepository driveRepository;
    private final RecruitmentStageRepository stageRepository;
    private final StudentRepository studentRepository;
    private final InterviewPanelRepository panelRepository;
    private final ApplicationRepository applicationRepository;
    private final InterviewEvaluationRepository evaluationRepository;
    private final RescheduleRequestRepository rescheduleRepository;
    private final SchedulingConflictService conflictService;
    private final NotificationService notificationService;

    public InterviewSchedulingService(
            InterviewRepository interviewRepository,
            PlacementDriveRepository driveRepository,
            RecruitmentStageRepository stageRepository,
            StudentRepository studentRepository,
            InterviewPanelRepository panelRepository,
            ApplicationRepository applicationRepository,
            InterviewEvaluationRepository evaluationRepository,
            RescheduleRequestRepository rescheduleRepository,
            SchedulingConflictService conflictService,
            NotificationService notificationService) {
        this.interviewRepository = interviewRepository;
        this.driveRepository = driveRepository;
        this.stageRepository = stageRepository;
        this.studentRepository = studentRepository;
        this.panelRepository = panelRepository;
        this.applicationRepository = applicationRepository;
        this.evaluationRepository = evaluationRepository;
        this.rescheduleRepository = rescheduleRepository;
        this.conflictService = conflictService;
        this.notificationService = notificationService;
    }

    @Transactional
    public List<InterviewResponseDto> scheduleInterviews(InterviewScheduleRequest req) {
        PlacementDrive drive = driveRepository.findById(req.getDriveId())
                .orElseThrow(() -> new RuntimeException("Drive not found: " + req.getDriveId()));

        RecruitmentStage stage = stageRepository.findById(req.getStageId())
                .orElseThrow(() -> new RuntimeException("Stage not found: " + req.getStageId()));

        InterviewPanel panel = req.getPanelId() != null
                ? panelRepository.findById(req.getPanelId()).orElse(null) : null;

        List<InterviewResponseDto> scheduled = new ArrayList<>();

        for (Long studentId : req.getStudentIds()) {
            Student student = studentRepository.findById(studentId).orElse(null);
            if (student == null) continue;

            Interview interview = new Interview();
            interview.setDrive(drive);
            interview.setStage(stage);
            interview.setStudent(student);
            interview.setInterviewDate(req.getInterviewDate());
            interview.setStartTime(req.getStartTime());
            interview.setEndTime(req.getEndTime());
            interview.setMode(req.getMode() != null ? req.getMode() : "ONLINE");
            interview.setMeetingLink(req.getMeetingLink());
            interview.setVenue(req.getVenue());
            interview.setPanel(panel);
            interview.setStatus(InterviewStatus.SCHEDULED);
            interview.setInstructions(req.getInstructions());
            interview.setRequiredDocuments(req.getRequiredDocuments());

            Interview saved = interviewRepository.save(interview);

            // Update candidate's application stage to INTERVIEW
            applicationRepository.findByDriveIdAndStudentId(drive.getId(), student.getId()).ifPresent(app -> {
                app.setStatus(ApplicationStatus.INTERVIEW);
                app.setCurrentStage(stage);
                applicationRepository.save(app);
            });

            // Check if there is an interview conflict with any other company
            ConflictReportDto conflict = conflictService.checkConflicts(
                    studentId, req.getInterviewDate(), req.getStartTime(), req.getEndTime(),
                    req.getMode(), req.getVenue(), req.getPanelId(), saved.getId());

            InterviewResponseDto resp = new InterviewResponseDto(saved);
            resp.setHasConflict(conflict.isHasConflict());
            if (conflict.isHasConflict()) {
                resp.setConflictReason(conflict.getSummary());

                // Send Conflict Alert to student
                if (student.getUser() != null) {
                    notificationService.sendNotification(
                            student.getUser().getId(),
                            "Interview Schedule Conflict Detected",
                            String.format("Warning: Your newly scheduled interview with %s overlaps with another existing interview on %s. You may request rescheduling.",
                                    drive.getCompany().getName(), req.getInterviewDate()),
                            "CONFLICT_ALERT",
                            saved.getId(),
                            "INTERVIEW"
                    );
                }
            } else {
                // Regular schedule notification
                if (student.getUser() != null) {
                    notificationService.sendNotification(
                            student.getUser().getId(),
                            "New Interview Scheduled",
                            String.format("%s scheduled your '%s' round on %s from %s to %s.",
                                    drive.getCompany().getName(), stage.getStageName(), req.getInterviewDate(), req.getStartTime(), req.getEndTime()),
                            "INTERVIEW_SCHEDULED",
                            saved.getId(),
                            "INTERVIEW"
                    );
                }
            }

            scheduled.add(resp);
        }

        return scheduled;
    }

    public List<InterviewResponseDto> getStudentCalendar(Long studentId) {
        return interviewRepository.findByStudentId(studentId).stream().map(i -> {
            InterviewResponseDto dto = new InterviewResponseDto(i);
            ConflictReportDto conflict = conflictService.checkConflicts(
                    studentId, i.getInterviewDate(), i.getStartTime(), i.getEndTime(),
                    i.getMode(), i.getVenue(), i.getPanel() != null ? i.getPanel().getId() : null, i.getId());
            dto.setHasConflict(conflict.isHasConflict());
            if (conflict.isHasConflict()) {
                dto.setConflictReason(conflict.getSummary());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    public List<InterviewResponseDto> getCentralCalendarForTpo(Long collegeId) {
        List<Interview> interviews = collegeId != null
                ? interviewRepository.findByCollegeId(collegeId)
                : interviewRepository.findAll();

        return interviews.stream().map(i -> {
            InterviewResponseDto dto = new InterviewResponseDto(i);
            ConflictReportDto conflict = conflictService.checkConflicts(
                    i.getStudent().getId(), i.getInterviewDate(), i.getStartTime(), i.getEndTime(),
                    i.getMode(), i.getVenue(), i.getPanel() != null ? i.getPanel().getId() : null, i.getId());
            dto.setHasConflict(conflict.isHasConflict());
            if (conflict.isHasConflict()) {
                dto.setConflictReason(conflict.getSummary());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    public List<InterviewResponseDto> getCompanyCalendar(Long companyId) {
        return interviewRepository.findByCompanyId(companyId).stream().map(InterviewResponseDto::new).collect(Collectors.toList());
    }

    // Reschedule Request Handling
    @Transactional
    public RescheduleRequest submitRescheduleRequest(Long interviewId, Long studentId, RescheduleSubmitRequest req) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found: " + interviewId));

        if (!interview.getStudent().getId().equals(studentId)) {
            throw new RuntimeException("Unauthorized: Cannot reschedule another candidate's interview.");
        }

        interview.setStatus(InterviewStatus.RESCHEDULE_REQUESTED);
        interviewRepository.save(interview);

        RescheduleRequest reschedule = new RescheduleRequest();
        reschedule.setInterview(interview);
        reschedule.setStudent(interview.getStudent());
        reschedule.setReason(req.getReason());
        reschedule.setPreferredAlternativeTime(req.getPreferredAlternativeTime());
        reschedule.setStatus(RescheduleStatus.PENDING);
        RescheduleRequest saved = rescheduleRepository.save(reschedule);

        // Notify TPO
        if (interview.getStudent().getCollege() != null) {
            // Broadcast notification to college TPOs
        }

        return saved;
    }

    @Transactional
    public RescheduleRequest reviewRescheduleRequest(Long requestId, RescheduleReviewRequest req, String reviewerName) {
        RescheduleRequest request = rescheduleRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Reschedule request not found: " + requestId));

        Interview interview = request.getInterview();

        if (req.isApproved()) {
            if (req.getNewDate() == null || req.getNewStartTime() == null || req.getNewEndTime() == null) {
                throw new RuntimeException("New date and time intervals must be provided for approval.");
            }

            // Conflict Recheck before confirming new slot!
            ConflictReportDto recheck = conflictService.checkConflicts(
                    interview.getStudent().getId(), req.getNewDate(), req.getNewStartTime(), req.getNewEndTime(),
                    interview.getMode(), interview.getVenue(),
                    interview.getPanel() != null ? interview.getPanel().getId() : null,
                    interview.getId());

            if (recheck.isHasConflict()) {
                throw new RuntimeException("Cannot reschedule to proposed time: " + recheck.getSummary());
            }

            interview.setInterviewDate(req.getNewDate());
            interview.setStartTime(req.getNewStartTime());
            interview.setEndTime(req.getNewEndTime());
            interview.setStatus(InterviewStatus.RESCHEDULED);
            interviewRepository.save(interview);

            request.setStatus(RescheduleStatus.APPROVED);
            request.setProposedNewDate(req.getNewDate());
            request.setProposedNewStartTime(req.getNewStartTime());
            request.setProposedNewEndTime(req.getNewEndTime());
        } else {
            interview.setStatus(InterviewStatus.SCHEDULED);
            interviewRepository.save(interview);
            request.setStatus(RescheduleStatus.REJECTED);
        }

        request.setTpoRemarks(req.getTpoRemarks());
        request.setReviewedBy(reviewerName);
        request.setReviewedAt(LocalDateTime.now());
        RescheduleRequest saved = rescheduleRepository.save(request);

        // Notify Student
        if (interview.getStudent() != null && interview.getStudent().getUser() != null) {
            notificationService.sendNotification(
                    interview.getStudent().getUser().getId(),
                    req.isApproved() ? "Interview Rescheduled Successfully" : "Reschedule Request Declined",
                    String.format("Your reschedule request for %s was %s. %s",
                            interview.getDrive().getCompany().getName(),
                            req.isApproved() ? "APPROVED: New slot " + req.getNewDate() + " " + req.getNewStartTime() : "DECLINED",
                            req.getTpoRemarks() != null ? "Remarks: " + req.getTpoRemarks() : ""),
                    "RESCHEDULE_UPDATE",
                    interview.getId(),
                    "INTERVIEW"
            );
        }

        return saved;
    }

    public List<RescheduleRequest> getPendingRescheduleRequests(Long collegeId) {
        if (collegeId != null) {
            return rescheduleRepository.findByCollegeId(collegeId).stream()
                    .filter(r -> r.getStatus() == RescheduleStatus.PENDING)
                    .collect(Collectors.toList());
        }
        return rescheduleRepository.findByStatus(RescheduleStatus.PENDING);
    }

    @Transactional
    public InterviewEvaluation recordEvaluation(Long interviewId, EvaluationRequest req, String evaluatorName) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found: " + interviewId));

        Application application = applicationRepository.findByDriveIdAndStudentId(
                interview.getDrive().getId(), interview.getStudent().getId())
                .orElseThrow(() -> new RuntimeException("Candidate application not found"));

        InterviewEvaluation eval = evaluationRepository.findByInterviewId(interviewId).orElse(new InterviewEvaluation());
        eval.setInterview(interview);
        eval.setApplication(application);
        eval.setTechnicalScore(req.getTechnicalScore());
        eval.setCommunicationScore(req.getCommunicationScore());
        eval.setProblemSolvingScore(req.getProblemSolvingScore());
        eval.setOverallRating(req.getOverallRating());
        eval.setComments(req.getComments());
        eval.setDecision(req.getDecision());
        eval.setEvaluatedBy(evaluatorName);
        eval.setEvaluatedAt(LocalDateTime.now());

        InterviewEvaluation savedEval = evaluationRepository.save(eval);

        // Advance Candidate Progression
        interview.setStatus(InterviewStatus.COMPLETED);
        interviewRepository.save(interview);

        List<RecruitmentStage> stages = stageRepository.findByDriveIdOrderByStageOrderAsc(interview.getDrive().getId());
        int currentOrder = interview.getStage().getStageOrder();

        if (req.getDecision() == EvaluationDecision.PASS) {
            RecruitmentStage nextStage = stages.stream().filter(s -> s.getStageOrder() > currentOrder).findFirst().orElse(null);
            if (nextStage != null) {
                application.setCurrentStage(nextStage);
                application.setStatus(ApplicationStatus.INTERVIEW);
            } else {
                // Cleared all rounds! Eligible for final offer
                application.setStatus(ApplicationStatus.SELECTED);
            }
        } else if (req.getDecision() == EvaluationDecision.FAIL) {
            application.setStatus(ApplicationStatus.REJECTED);
        } else {
            application.setStatus(ApplicationStatus.UNDER_REVIEW);
        }
        applicationRepository.save(application);

        return savedEval;
    }

    public Optional<InterviewEvaluation> getEvaluationByInterviewId(Long interviewId) {
        return evaluationRepository.findByInterviewId(interviewId);
    }
}

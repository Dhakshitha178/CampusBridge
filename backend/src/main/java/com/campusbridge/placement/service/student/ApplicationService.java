package com.campusbridge.placement.service.student;

import com.campusbridge.placement.dto.request.ApplicationRequest;
import com.campusbridge.placement.dto.request.BulkResultRowDto;
import com.campusbridge.placement.dto.response.ApplicationStatusResponse;
import com.campusbridge.placement.dto.response.EligibilityResultDto;
import com.campusbridge.placement.entity.*;
import com.campusbridge.placement.entity.enums.ApplicationStatus;
import com.campusbridge.placement.entity.enums.EvaluationDecision;
import com.campusbridge.placement.repository.*;
import com.campusbridge.placement.service.eligibility.EligibilityCheckService;
import com.campusbridge.placement.service.notification.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationStatusHistoryRepository historyRepository;
    private final PlacementDriveRepository driveRepository;
    private final StudentRepository studentRepository;
    private final RecruitmentStageRepository stageRepository;
    private final EligibilityCheckService eligibilityCheckService;
    private final NotificationService notificationService;

    public ApplicationService(
            ApplicationRepository applicationRepository,
            ApplicationStatusHistoryRepository historyRepository,
            PlacementDriveRepository driveRepository,
            StudentRepository studentRepository,
            RecruitmentStageRepository stageRepository,
            EligibilityCheckService eligibilityCheckService,
            NotificationService notificationService) {
        this.applicationRepository = applicationRepository;
        this.historyRepository = historyRepository;
        this.driveRepository = driveRepository;
        this.studentRepository = studentRepository;
        this.stageRepository = stageRepository;
        this.eligibilityCheckService = eligibilityCheckService;
        this.notificationService = notificationService;
    }

    @Transactional
    public ApplicationStatusResponse apply(Long studentId, ApplicationRequest req) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student profile not found: " + studentId));

        PlacementDrive drive = driveRepository.findById(req.getDriveId())
                .orElseThrow(() -> new RuntimeException("Drive not found: " + req.getDriveId()));

        // 1. Prevent duplicate applications
        if (applicationRepository.existsByDriveIdAndStudentId(drive.getId(), student.getId())) {
            throw new RuntimeException("You have already applied for this placement drive.");
        }

        // 2. Validate application deadline
        if (drive.getApplicationDeadline() != null && LocalDateTime.now().isAfter(drive.getApplicationDeadline())) {
            throw new RuntimeException("Application deadline has passed.");
        }

        // 3. Automated Eligibility Check
        EligibilityResultDto eligibility = eligibilityCheckService.checkEligibility(student, drive.getEligibilityCriteria());
        if (!eligibility.isEligible()) {
            throw new RuntimeException("Ineligible to apply: " + String.join(", ", eligibility.getReasons()));
        }

        // 4. Initial stage
        List<RecruitmentStage> stages = stageRepository.findByDriveIdOrderByStageOrderAsc(drive.getId());
        RecruitmentStage initialStage = stages.isEmpty() ? null : stages.get(0);

        Application application = new Application();
        application.setDrive(drive);
        application.setStudent(student);
        application.setStatus(ApplicationStatus.APPLIED);
        application.setCurrentStage(initialStage);
        application.setNotes(req.getNotes());
        application.setAppliedDate(LocalDateTime.now());

        Application saved = applicationRepository.save(application);

        // Audit history
        historyRepository.save(new ApplicationStatusHistory(
                saved, ApplicationStatus.APPLIED.name(),
                initialStage != null ? initialStage.getStageName() : "Applied",
                "Application submitted successfully",
                student.getFullName()
        ));

        // Notify Recruiter
        if (drive.getCreatedBy() != null) {
            notificationService.sendNotification(
                    drive.getCreatedBy().getId(),
                    "New Candidate Application",
                    String.format("%s (%s, CGPA: %.2f) applied for %s.",
                            student.getFullName(), student.getDepartment(), student.getCgpa(), drive.getJobTitle()),
                    "NEW_APPLICATION",
                    saved.getId(),
                    "APPLICATION"
            );
        }

        return new ApplicationStatusResponse(saved);
    }

    @Transactional
    public ApplicationStatusResponse withdraw(Long applicationId, Long studentId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found: " + applicationId));

        if (!application.getStudent().getId().equals(studentId)) {
            throw new RuntimeException("Unauthorized: Cannot withdraw another student's application.");
        }

        if (application.getStatus() == ApplicationStatus.SELECTED) {
            throw new RuntimeException("Cannot withdraw after offer selection.");
        }

        application.setStatus(ApplicationStatus.WITHDRAWN);
        Application updated = applicationRepository.save(application);

        historyRepository.save(new ApplicationStatusHistory(
                updated, ApplicationStatus.WITHDRAWN.name(),
                "Withdrawn",
                "Application withdrawn by student",
                application.getStudent().getFullName()
        ));

        return new ApplicationStatusResponse(updated);
    }

    public List<ApplicationStatusResponse> getStudentApplications(Long studentId) {
        return applicationRepository.findByStudentId(studentId).stream()
                .map(ApplicationStatusResponse::new)
                .collect(Collectors.toList());
    }

    public List<ApplicationStatusResponse> getDriveApplications(Long driveId) {
        return applicationRepository.findByDriveId(driveId).stream()
                .map(ApplicationStatusResponse::new)
                .collect(Collectors.toList());
    }

    public List<ApplicationStatusResponse> getCompanyApplications(Long companyId) {
        return applicationRepository.findByCompanyId(companyId).stream()
                .map(ApplicationStatusResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public ApplicationStatusResponse updateStage(Long applicationId, Long nextStageId, ApplicationStatus status, String remarks, String updatedBy) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found: " + applicationId));

        if (nextStageId != null) {
            stageRepository.findById(nextStageId).ifPresent(app::setCurrentStage);
        }
        if (status != null) {
            app.setStatus(status);
        }
        app.setUpdatedAt(LocalDateTime.now());
        Application saved = applicationRepository.save(app);

        String stageName = app.getCurrentStage() != null ? app.getCurrentStage().getStageName() : status.name();
        historyRepository.save(new ApplicationStatusHistory(saved, status.name(), stageName, remarks, updatedBy));

        // Notify student
        if (app.getStudent() != null && app.getStudent().getUser() != null) {
            notificationService.sendNotification(
                    app.getStudent().getUser().getId(),
                    "Application Status Update",
                    String.format("Your application for %s at %s moved to '%s' (%s).",
                            app.getDrive().getJobTitle(), app.getDrive().getCompany().getName(), stageName, status),
                    "STATUS_UPDATE",
                    saved.getId(),
                    "APPLICATION"
            );
        }

        return new ApplicationStatusResponse(saved);
    }

    @Transactional
    public List<ApplicationStatusResponse> bulkProcessResults(Long driveId, List<BulkResultRowDto> rows, String updatedBy) {
        List<ApplicationStatusResponse> responses = new ArrayList<>();
        List<RecruitmentStage> stages = stageRepository.findByDriveIdOrderByStageOrderAsc(driveId);

        for (BulkResultRowDto row : rows) {
            Student student = studentRepository.findByRollNumber(row.getRollNumber()).orElse(null);
            if (student == null) continue;

            Application app = applicationRepository.findByDriveIdAndStudentId(driveId, student.getId()).orElse(null);
            if (app == null) continue;

            ApplicationStatus newStatus;
            RecruitmentStage nextStage = app.getCurrentStage();

            if (row.getDecision() == EvaluationDecision.PASS) {
                // Advance to next stage if available
                int currentOrder = app.getCurrentStage() != null ? app.getCurrentStage().getStageOrder() : 0;
                RecruitmentStage subsequent = stages.stream().filter(s -> s.getStageOrder() > currentOrder).findFirst().orElse(null);
                if (subsequent != null) {
                    nextStage = subsequent;
                    newStatus = ApplicationStatus.INTERVIEW;
                } else {
                    newStatus = ApplicationStatus.SELECTED;
                }
            } else if (row.getDecision() == EvaluationDecision.FAIL) {
                newStatus = ApplicationStatus.REJECTED;
            } else {
                newStatus = ApplicationStatus.UNDER_REVIEW;
            }

            app.setStatus(newStatus);
            app.setCurrentStage(nextStage);
            app.setUpdatedAt(LocalDateTime.now());
            Application saved = applicationRepository.save(app);

            historyRepository.save(new ApplicationStatusHistory(
                    saved, newStatus.name(), nextStage != null ? nextStage.getStageName() : "Evaluated",
                    row.getComments() != null ? row.getComments() : "Bulk imported result: Score " + row.getScore(),
                    updatedBy
            ));

            responses.add(new ApplicationStatusResponse(saved));
        }

        return responses;
    }
}

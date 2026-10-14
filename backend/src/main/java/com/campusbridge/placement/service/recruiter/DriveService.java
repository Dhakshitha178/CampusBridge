package com.campusbridge.placement.service.recruiter;

import com.campusbridge.placement.dto.request.DriveApprovalRequest;
import com.campusbridge.placement.dto.request.DriveCreateRequest;
import com.campusbridge.placement.dto.response.DriveResponse;
import com.campusbridge.placement.dto.response.EligibilityResultDto;
import com.campusbridge.placement.entity.*;
import com.campusbridge.placement.entity.enums.DriveStatus;
import com.campusbridge.placement.entity.enums.StageType;
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
public class DriveService {

    private final PlacementDriveRepository driveRepository;
    private final DriveEligibilityCriteriaRepository criteriaRepository;
    private final RecruitmentStageRepository stageRepository;
    private final CollegeRepository collegeRepository;
    private final StudentRepository studentRepository;
    private final ApplicationRepository applicationRepository;
    private final EligibilityCheckService eligibilityCheckService;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public DriveService(
            PlacementDriveRepository driveRepository,
            DriveEligibilityCriteriaRepository criteriaRepository,
            RecruitmentStageRepository stageRepository,
            CollegeRepository collegeRepository,
            StudentRepository studentRepository,
            ApplicationRepository applicationRepository,
            EligibilityCheckService eligibilityCheckService,
            NotificationService notificationService,
            UserRepository userRepository) {
        this.driveRepository = driveRepository;
        this.criteriaRepository = criteriaRepository;
        this.stageRepository = stageRepository;
        this.collegeRepository = collegeRepository;
        this.studentRepository = studentRepository;
        this.applicationRepository = applicationRepository;
        this.eligibilityCheckService = eligibilityCheckService;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    @Transactional
    public DriveResponse createDrive(Recruiter recruiter, DriveCreateRequest req) {
        PlacementDrive drive = new PlacementDrive();
        drive.setCompany(recruiter.getCompany());
        drive.setJobTitle(req.getJobTitle());
        drive.setJobDescription(req.getJobDescription());
        drive.setPackageLpa(req.getPackageLpa());
        drive.setLocation(req.getLocation());
        drive.setWorkMode(req.getWorkMode());
        drive.setVacancies(req.getVacancies());
        drive.setEmploymentType(req.getEmploymentType());
        drive.setApplicationDeadline(req.getApplicationDeadline());
        drive.setDriveDate(req.getDriveDate());
        drive.setStatus(DriveStatus.PENDING_APPROVAL); // Requires TPO approval
        drive.setCreatedBy(recruiter.getUser());

        if (req.getCollegeId() != null) {
            collegeRepository.findById(req.getCollegeId()).ifPresent(drive::setCollege);
        }

        PlacementDrive savedDrive = driveRepository.save(drive);

        // 1. Save Eligibility Criteria
        DriveEligibilityCriteria criteria = new DriveEligibilityCriteria(
                savedDrive,
                req.getAllowedDepartments(),
                req.getMinCgpa(),
                req.getMaxBacklogs(),
                req.getGraduationYear(),
                req.getRequiredSkills()
        );
        criteriaRepository.save(criteria);
        savedDrive.setEligibilityCriteria(criteria);

        // 2. Save Configurable Recruitment Stages
        List<RecruitmentStage> stages = new ArrayList<>();
        if (req.getStages() != null && !req.getStages().isEmpty()) {
            for (DriveCreateRequest.StageRequest sReq : req.getStages()) {
                RecruitmentStage stage = new RecruitmentStage(
                        savedDrive,
                        sReq.getStageOrder(),
                        sReq.getStageName(),
                        sReq.getStageType() != null ? sReq.getStageType() : StageType.TECHNICAL_INTERVIEW,
                        sReq.getDescription(),
                        Boolean.TRUE.equals(sReq.getIsFinalStage())
                );
                stage.setCutOffScore(sReq.getCutOffScore());
                stages.add(stage);
            }
        } else {
            // Default configurable pipeline: Assessment -> Technical -> HR
            stages.add(new RecruitmentStage(savedDrive, 1, "Online Assessment", StageType.APTITUDE, "Initial aptitude and coding test", false));
            stages.add(new RecruitmentStage(savedDrive, 2, "Technical Interview", StageType.TECHNICAL_INTERVIEW, "Core domain & coding interview", false));
            stages.add(new RecruitmentStage(savedDrive, 3, "HR & Cultural Fit", StageType.HR_INTERVIEW, "HR discussion and offer finalization", true));
        }
        stageRepository.saveAll(stages);
        savedDrive.setRecruitmentStages(stages);

        // Notify TPO users
        userRepository.findByRole(com.campusbridge.placement.entity.enums.Role.TPO).forEach(tpo -> {
            notificationService.sendNotification(
                    tpo.getId(),
                    "New Drive Submitted for Approval",
                    String.format("%s submitted a new drive for '%s' (%.1f LPA). Awaiting your review.",
                            recruiter.getCompany().getName(), savedDrive.getJobTitle(), savedDrive.getPackageLpa()),
                    "DRIVE_SUBMISSION",
                    savedDrive.getId(),
                    "DRIVE"
            );
        });

        return new DriveResponse(savedDrive);
    }

    @Transactional
    public DriveResponse reviewDrive(Long driveId, DriveApprovalRequest req, String reviewerName) {
        PlacementDrive drive = driveRepository.findById(driveId)
                .orElseThrow(() -> new RuntimeException("Drive not found: " + driveId));

        if (req.isApproved()) {
            drive.setStatus(DriveStatus.ACTIVE);
            drive.setTpoApprovalNotes("Approved: " + (req.getNotes() != null ? req.getNotes() : "Drive published to eligible students."));
        } else {
            drive.setStatus(DriveStatus.REJECTED);
            drive.setTpoApprovalNotes("Rejected: " + (req.getNotes() != null ? req.getNotes() : "Criteria does not match placement policy."));
        }

        PlacementDrive updated = driveRepository.save(drive);

        // Notify Recruiter
        if (drive.getCreatedBy() != null) {
            notificationService.sendNotification(
                    drive.getCreatedBy().getId(),
                    req.isApproved() ? "Drive Approved & Published" : "Drive Submission Rejected",
                    String.format("Your drive for '%s' was %s by TPO. Notes: %s",
                            drive.getJobTitle(), req.isApproved() ? "APPROVED" : "REJECTED", drive.getTpoApprovalNotes()),
                    "DRIVE_APPROVAL",
                    drive.getId(),
                    "DRIVE"
            );
        }

        return new DriveResponse(updated);
    }

    public List<DriveResponse> getActiveDrivesForStudent(Long studentId) {
        Student student = studentId != null ? studentRepository.findById(studentId).orElse(null) : null;
        Long collegeId = student != null && student.getCollege() != null ? student.getCollege().getId() : null;

        List<PlacementDrive> drives = collegeId != null
                ? driveRepository.findActiveDrivesForCollege(collegeId)
                : driveRepository.findActiveDrives();

        return drives.stream().map(d -> {
            DriveResponse resp = new DriveResponse(d);
            resp.setTotalApplications(applicationRepository.countByDriveId(d.getId()));
            if (student != null) {
                EligibilityResultDto elig = eligibilityCheckService.checkEligibility(student, d.getEligibilityCriteria());
                resp.setStudentEligibility(elig);
            }
            return resp;
        }).collect(Collectors.toList());
    }

    public List<DriveResponse> getAllDrivesForTpo() {
        return driveRepository.findAll().stream().map(d -> {
            DriveResponse resp = new DriveResponse(d);
            resp.setTotalApplications(applicationRepository.countByDriveId(d.getId()));
            return resp;
        }).collect(Collectors.toList());
    }

    public List<DriveResponse> getDrivesForCompany(Long companyId) {
        return driveRepository.findByCompanyId(companyId).stream().map(d -> {
            DriveResponse resp = new DriveResponse(d);
            resp.setTotalApplications(applicationRepository.countByDriveId(d.getId()));
            return resp;
        }).collect(Collectors.toList());
    }

    public DriveResponse getDriveById(Long driveId, Long studentId) {
        PlacementDrive drive = driveRepository.findById(driveId)
                .orElseThrow(() -> new RuntimeException("Drive not found: " + driveId));
        DriveResponse resp = new DriveResponse(drive);
        resp.setTotalApplications(applicationRepository.countByDriveId(driveId));

        if (studentId != null) {
            studentRepository.findById(studentId).ifPresent(s -> {
                resp.setStudentEligibility(eligibilityCheckService.checkEligibility(s, drive.getEligibilityCriteria()));
            });
        }
        return resp;
    }
}

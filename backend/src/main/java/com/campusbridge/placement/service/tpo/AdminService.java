package com.campusbridge.placement.service.tpo;

import com.campusbridge.placement.dto.request.AnnouncementCreateRequest;
import com.campusbridge.placement.dto.response.LiveDriveMonitorDto;
import com.campusbridge.placement.dto.response.StudentResponse;
import com.campusbridge.placement.dto.response.TpoDashboardStatsDto;
import com.campusbridge.placement.entity.*;
import com.campusbridge.placement.entity.enums.*;
import com.campusbridge.placement.repository.*;
import com.campusbridge.placement.service.interview.SchedulingConflictService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final StudentRepository studentRepository;
    private final CompanyRepository companyRepository;
    private final PlacementDriveRepository driveRepository;
    private final ApplicationRepository applicationRepository;
    private final InterviewRepository interviewRepository;
    private final OfferRepository offerRepository;
    private final RescheduleRequestRepository rescheduleRepository;
    private final AnnouncementRepository announcementRepository;
    private final CollegeRepository collegeRepository;
    private final SchedulingConflictService conflictService;
    private final RecruitmentStageRepository stageRepository;

    public AdminService(
            StudentRepository studentRepository,
            CompanyRepository companyRepository,
            PlacementDriveRepository driveRepository,
            ApplicationRepository applicationRepository,
            InterviewRepository interviewRepository,
            OfferRepository offerRepository,
            RescheduleRequestRepository rescheduleRepository,
            AnnouncementRepository announcementRepository,
            CollegeRepository collegeRepository,
            SchedulingConflictService conflictService,
            RecruitmentStageRepository stageRepository) {
        this.studentRepository = studentRepository;
        this.companyRepository = companyRepository;
        this.driveRepository = driveRepository;
        this.applicationRepository = applicationRepository;
        this.interviewRepository = interviewRepository;
        this.offerRepository = offerRepository;
        this.rescheduleRepository = rescheduleRepository;
        this.announcementRepository = announcementRepository;
        this.collegeRepository = collegeRepository;
        this.conflictService = conflictService;
        this.stageRepository = stageRepository;
    }

    public TpoDashboardStatsDto getDashboardStats(Long collegeId) {
        List<Student> students = collegeId != null ? studentRepository.findByCollegeId(collegeId) : studentRepository.findAll();
        List<PlacementDrive> drives = collegeId != null ? driveRepository.findByCollegeId(collegeId) : driveRepository.findAll();
        List<Application> apps = collegeId != null ? applicationRepository.findByCollegeId(collegeId) : applicationRepository.findAll();
        List<Offer> offers = collegeId != null ? offerRepository.findByCollegeId(collegeId) : offerRepository.findAll();
        List<Interview> interviews = collegeId != null ? interviewRepository.findByCollegeId(collegeId) : interviewRepository.findAll();

        TpoDashboardStatsDto dto = new TpoDashboardStatsDto();
        dto.setTotalStudents(students.size());

        long placed = students.stream().filter(s -> s.getPlacementStatus() == PlacementStatus.PLACED).count();
        dto.setPlacedStudents(placed);

        dto.setActiveCompanies(companyRepository.count());
        dto.setActiveDrives(drives.stream().filter(d -> d.getStatus() == DriveStatus.ACTIVE).count());
        dto.setPendingApprovalDrives(drives.stream().filter(d -> d.getStatus() == DriveStatus.PENDING_APPROVAL).count());
        dto.setTotalApplications(apps.size());

        long shortlisted = apps.stream().filter(a ->
                a.getStatus() == ApplicationStatus.SHORTLISTED ||
                a.getStatus() == ApplicationStatus.ASSESSMENT ||
                a.getStatus() == ApplicationStatus.INTERVIEW ||
                a.getStatus() == ApplicationStatus.SELECTED).count();
        dto.setShortlistedCount(shortlisted);

        LocalDate today = LocalDate.now();
        long interviewsToday = interviews.stream().filter(i -> i.getInterviewDate().isEqual(today)).count();
        dto.setInterviewsToday(interviewsToday);

        dto.setTotalOffers(offers.size());

        // Count detected conflicts
        long conflicts = 0;
        for (Interview i : interviews) {
            if (i.getStatus() == InterviewStatus.SCHEDULED) {
                if (conflictService.checkConflicts(i.getStudent().getId(), i.getInterviewDate(), i.getStartTime(), i.getEndTime(),
                        i.getMode(), i.getVenue(), null, i.getId()).isHasConflict()) {
                    conflicts++;
                }
            }
        }
        dto.setDetectedConflicts(conflicts);

        long pendingReschedules = rescheduleRepository.findByStatus(RescheduleStatus.PENDING).size();
        dto.setPendingRescheduleRequests(pendingReschedules);

        double pct = students.size() > 0 ? ((double) placed / students.size()) * 100.0 : 0.0;
        dto.setPlacementPercentage(Math.round(pct * 10.0) / 10.0);

        double avgPkg = offers.stream().mapToDouble(Offer::getPackageLpa).average().orElse(0.0);
        double maxPkg = offers.stream().mapToDouble(Offer::getPackageLpa).max().orElse(0.0);
        dto.setAvgPackage(Math.round(avgPkg * 100.0) / 100.0);
        dto.setHighestPackage(Math.round(maxPkg * 100.0) / 100.0);

        return dto;
    }

    public List<StudentResponse> searchStudents(Long collegeId, String department, Double minCgpa, Integer maxBacklogs, PlacementStatus status) {
        List<Student> students = collegeId != null ? studentRepository.findByCollegeId(collegeId) : studentRepository.findAll();

        return students.stream()
                .filter(s -> department == null || department.isEmpty() || department.equalsIgnoreCase("ALL") || s.getDepartment().equalsIgnoreCase(department))
                .filter(s -> minCgpa == null || (s.getCgpa() != null && s.getCgpa() >= minCgpa))
                .filter(s -> maxBacklogs == null || (s.getBacklogs() != null && s.getBacklogs() <= maxBacklogs))
                .filter(s -> status == null || s.getPlacementStatus() == status)
                .map(StudentResponse::new)
                .collect(Collectors.toList());
    }

    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    @Transactional
    public Company verifyCompany(Long companyId, boolean verified) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found: " + companyId));
        company.setVerified(verified);
        return companyRepository.save(company);
    }

    /**
     * Multi-Company Live Drive Monitor:
     * Company -> Current Stage -> Number of Candidates -> Stage Status -> Pending Actions
     */
    public List<LiveDriveMonitorDto> getLiveDriveMonitor(Long collegeId) {
        List<PlacementDrive> drives = collegeId != null
                ? driveRepository.findActiveDrivesForCollege(collegeId)
                : driveRepository.findActiveDrives();

        List<LiveDriveMonitorDto> monitors = new ArrayList<>();

        for (PlacementDrive drive : drives) {
            List<Application> apps = applicationRepository.findByDriveId(drive.getId());
            long totalApplicants = apps.size();

            long selectedCount = apps.stream()
                    .filter(a -> a.getStatus() == ApplicationStatus.SELECTED)
                    .count();

            long rejectedCount = apps.stream()
                    .filter(a -> a.getStatus() == ApplicationStatus.REJECTED || a.getStatus() == ApplicationStatus.NOT_SELECTED)
                    .count();

            long inProgressCount = totalApplicants - selectedCount - rejectedCount;
            if (inProgressCount < 0) inProgressCount = 0;

            List<RecruitmentStage> stages = stageRepository.findByDriveIdOrderByStageOrderAsc(drive.getId());
            if ((stages == null || stages.isEmpty()) && drive.getRecruitmentStages() != null) {
                stages = drive.getRecruitmentStages();
            }

            List<LiveDriveMonitorDto.StageBreakdownDto> stageBreakdown = new ArrayList<>();
            String currentStageName = "Online Assessment";
            int stageOrder = 1;
            long candidatesInStage = inProgressCount;
            String stageStatus = "Round 1 Active";
            String pendingActions = totalApplicants > 0
                    ? totalApplicants + " candidate(s) enrolled in recruitment pipeline"
                    : "No candidates applied yet";

            if (stages != null && !stages.isEmpty()) {
                RecruitmentStage activeStage = stages.get(0);
                for (RecruitmentStage s : stages) {
                    long count = apps.stream()
                            .filter(a -> a.getCurrentStage() != null && a.getCurrentStage().getId().equals(s.getId()))
                            .count();
                    stageBreakdown.add(new LiveDriveMonitorDto.StageBreakdownDto(s.getStageName(), count));
                    if (count > 0) {
                        activeStage = s;
                        candidatesInStage = count;
                    }
                }

                currentStageName = activeStage.getStageName();
                stageOrder = activeStage.getStageOrder();
                stageStatus = "Round " + stageOrder + " Active";
                if (candidatesInStage > 0) {
                    pendingActions = candidatesInStage + " candidate(s) in " + currentStageName;
                }
            }

            LiveDriveMonitorDto dto = new LiveDriveMonitorDto();
            dto.setDriveId(drive.getId());
            dto.setCompanyName(drive.getCompany() != null ? drive.getCompany().getName() : "");
            dto.setCompanyLogo(drive.getCompany() != null ? drive.getCompany().getLogoUrl() : "");
            dto.setDriveTitle(drive.getJobTitle());
            dto.setJobTitle(drive.getJobTitle());
            dto.setDriveStatus(drive.getStatus() != null ? drive.getStatus().name() : "ACTIVE");
            dto.setSalaryPackage(drive.getPackageLpa());
            dto.setPackageLpa(drive.getPackageLpa());
            dto.setWorkMode(drive.getWorkMode() != null ? drive.getWorkMode().name() : "ON_SITE");
            dto.setVacancies(drive.getVacancies() != null ? drive.getVacancies() : 0);
            dto.setCurrentActiveStage(currentStageName);
            dto.setCurrentStage(currentStageName);
            dto.setStageOrder(stageOrder);
            dto.setCandidatesInStage(candidatesInStage);
            dto.setTotalApplicants(totalApplicants);
            dto.setTotalDriveApplicants(totalApplicants);
            dto.setInProgressCount(inProgressCount);
            dto.setSelectedCount(selectedCount);
            dto.setRejectedCount(rejectedCount);
            dto.setStageStatus(stageStatus);
            dto.setPendingActions(pendingActions);
            dto.setStageBreakdown(stageBreakdown);

            monitors.add(dto);
        }

        return monitors;
    }

    // Announcements
    @Transactional
    public Announcement createAnnouncement(Long collegeId, AnnouncementCreateRequest req, String postedBy) {
        College college = collegeId != null ? collegeRepository.findById(collegeId).orElse(null) : null;
        if (college == null) {
            college = collegeRepository.findAll().stream().findFirst().orElse(null);
        }

        Company company = req.getCompanyId() != null ? companyRepository.findById(req.getCompanyId()).orElse(null) : null;

        Announcement announcement = new Announcement(
                college,
                req.getTitle(),
                req.getContent(),
                req.getTargetAudience() != null ? req.getTargetAudience() : TargetAudience.ALL,
                req.getTargetDepartment(),
                company,
                postedBy
        );

        return announcementRepository.save(announcement);
    }

    public List<Announcement> getAnnouncements(Long collegeId) {
        return collegeId != null
                ? announcementRepository.findAllForCollege(collegeId)
                : announcementRepository.findAll();
    }
}

package com.campusbridge.placement.service.student;

import com.campusbridge.placement.dto.request.StudentProfileUpdateRequest;
import com.campusbridge.placement.dto.response.StudentDashboardStatsDto;
import com.campusbridge.placement.dto.response.StudentResponse;
import com.campusbridge.placement.entity.*;
import com.campusbridge.placement.entity.enums.ApplicationStatus;
import com.campusbridge.placement.entity.enums.InterviewStatus;
import com.campusbridge.placement.entity.enums.OfferStatus;
import com.campusbridge.placement.repository.*;
import com.campusbridge.placement.service.interview.SchedulingConflictService;
import com.campusbridge.placement.service.notification.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final InterviewRepository interviewRepository;
    private final OfferRepository offerRepository;
    private final RescheduleRequestRepository rescheduleRepository;
    private final NotificationService notificationService;
    private final SchedulingConflictService conflictService;
    private final CompanyPlacementHistoryRepository historyRepository;

    public StudentService(
            StudentRepository studentRepository,
            UserRepository userRepository,
            ApplicationRepository applicationRepository,
            InterviewRepository interviewRepository,
            OfferRepository offerRepository,
            RescheduleRequestRepository rescheduleRepository,
            NotificationService notificationService,
            SchedulingConflictService conflictService,
            CompanyPlacementHistoryRepository historyRepository) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
        this.interviewRepository = interviewRepository;
        this.offerRepository = offerRepository;
        this.rescheduleRepository = rescheduleRepository;
        this.notificationService = notificationService;
        this.conflictService = conflictService;
        this.historyRepository = historyRepository;
    }

    public StudentResponse getProfile(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));
        return new StudentResponse(student);
    }

    public StudentResponse getProfileByUserId(Long userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Student profile not found for user: " + userId));
        return new StudentResponse(student);
    }

    @Transactional
    public StudentResponse updateProfile(Long studentId, StudentProfileUpdateRequest req) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));

        if (req.getFullName() != null && student.getUser() != null) {
            student.getUser().setFullName(req.getFullName());
        }
        if (req.getPhoneNumber() != null && student.getUser() != null) {
            student.getUser().setPhoneNumber(req.getPhoneNumber());
        }
        if (student.getUser() != null) {
            userRepository.save(student.getUser());
        }

        if (req.getDepartment() != null) student.setDepartment(req.getDepartment());
        if (req.getAcademicYear() != null) student.setAcademicYear(req.getAcademicYear());
        if (req.getGraduationYear() != null) student.setGraduationYear(req.getGraduationYear());
        if (req.getCgpa() != null) student.setCgpa(req.getCgpa());
        if (req.getBacklogs() != null) student.setBacklogs(req.getBacklogs());
        if (req.getSkills() != null) student.setSkills(req.getSkills());
        if (req.getCertifications() != null) student.setCertifications(req.getCertifications());
        if (req.getResumeUrl() != null) {
            student.setResumeUrl(req.getResumeUrl());
            student.setResumeFileName("Resume_" + student.getRollNumber() + ".pdf");
        }

        Student saved = studentRepository.save(student);
        return new StudentResponse(saved);
    }

    public StudentDashboardStatsDto getDashboardStats(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));

        List<Application> apps = applicationRepository.findByStudentId(studentId);
        List<Interview> interviews = interviewRepository.findByStudentId(studentId);
        List<Offer> offers = offerRepository.findByStudentId(studentId);

        StudentDashboardStatsDto dto = new StudentDashboardStatsDto();
        dto.setTotalApplications(apps.size());

        long shortlisted = apps.stream().filter(a ->
                a.getStatus() == ApplicationStatus.SHORTLISTED ||
                a.getStatus() == ApplicationStatus.ASSESSMENT ||
                a.getStatus() == ApplicationStatus.INTERVIEW ||
                a.getStatus() == ApplicationStatus.SELECTED).count();
        dto.setShortlistedCount(shortlisted);

        long active = apps.stream().filter(a ->
                a.getStatus() != ApplicationStatus.REJECTED &&
                a.getStatus() != ApplicationStatus.NOT_SELECTED &&
                a.getStatus() != ApplicationStatus.WITHDRAWN).count();
        dto.setActiveProcesses(active);

        LocalDate today = LocalDate.now();
        long upcoming = interviews.stream().filter(i ->
                i.getStatus() == InterviewStatus.SCHEDULED &&
                (i.getInterviewDate().isAfter(today) || i.getInterviewDate().isEqual(today))).count();
        dto.setUpcomingInterviews(upcoming);

        long completed = interviews.stream().filter(i -> i.getStatus() == InterviewStatus.COMPLETED).count();
        dto.setCompletedInterviews(completed);

        long validOffers = offers.stream().filter(o -> o.getOfferStatus() != OfferStatus.WITHDRAWN).count();
        dto.setOffersReceived(validOffers);

        // Conflict count
        long conflictCount = 0;
        for (Interview i : interviews) {
            if (i.getStatus() == InterviewStatus.SCHEDULED) {
                if (conflictService.checkConflicts(studentId, i.getInterviewDate(), i.getStartTime(), i.getEndTime(),
                        i.getMode(), i.getVenue(), null, i.getId()).isHasConflict()) {
                    conflictCount++;
                }
            }
        }
        dto.setDetectedConflicts(conflictCount);

        long pendingReschedules = rescheduleRepository.findByStudentId(studentId).stream()
                .filter(r -> r.getStatus() == com.campusbridge.placement.entity.enums.RescheduleStatus.PENDING).count();
        dto.setPendingRescheduleRequests(pendingReschedules);

        if (student.getUser() != null) {
            dto.setUnreadNotifications(notificationService.getUnreadCount(student.getUser().getId()));
        }

        dto.setProfileCgpa(student.getCgpa());
        dto.setPlacementStatus(student.getPlacementStatus().name());

        return dto;
    }

    public List<Map<String, Object>> getMultiCompanyTracker(Long studentId) {
        List<Application> apps = applicationRepository.findByStudentId(studentId);
        List<Interview> interviews = interviewRepository.findByStudentId(studentId);
        LocalDate today = LocalDate.now();

        List<Map<String, Object>> tracker = new ArrayList<>();
        for (Application a : apps) {
            Map<String, Object> map = new HashMap<>();
            map.put("applicationId", a.getId());
            map.put("companyName", a.getDrive().getCompany().getName());
            map.put("companyLogo", a.getDrive().getCompany().getLogoUrl());
            map.put("jobTitle", a.getDrive().getJobTitle());
            map.put("packageLpa", a.getDrive().getPackageLpa());
            map.put("applicationStatus", a.getStatus().name());
            map.put("currentStage", a.getCurrentStage() != null ? a.getCurrentStage().getStageName() : "Initial Review");
            map.put("stageType", a.getCurrentStage() != null ? a.getCurrentStage().getStageType().name() : "APPLIED");
            map.put("lastUpdate", a.getUpdatedAt());

            // Next interview
            Interview nextInterview = interviews.stream()
                    .filter(i -> i.getDrive().getId().equals(a.getDrive().getId()) &&
                            i.getStatus() == InterviewStatus.SCHEDULED &&
                            (i.getInterviewDate().isAfter(today) || i.getInterviewDate().isEqual(today)))
                    .min(Comparator.comparing(Interview::getInterviewDate).thenComparing(Interview::getStartTime))
                    .orElse(null);

            if (nextInterview != null) {
                map.put("nextInterviewDate", nextInterview.getInterviewDate());
                map.put("nextInterviewTime", nextInterview.getStartTime() + " - " + nextInterview.getEndTime());
                map.put("nextInterviewMode", nextInterview.getMode());
            } else {
                map.put("nextInterviewDate", null);
            }

            tracker.add(map);
        }
        return tracker;
    }

    public List<CompanyPlacementHistory> getCompanyPlacementHistory(Long collegeId) {
        if (collegeId != null) {
            return historyRepository.findByCollegeId(collegeId);
        }
        return historyRepository.findAll();
    }

    public List<Map<String, Object>> getInterCollegeAggregatedHistory() {
        List<CompanyPlacementHistory> all = historyRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (CompanyPlacementHistory h : all) {
            Map<String, Object> m = new HashMap<>();
            m.put("collegeName", h.getCollege() != null ? h.getCollege().getName() : "Ecosystem Member");
            m.put("collegeCode", h.getCollege() != null ? h.getCollege().getCode() : "COL");
            m.put("companyName", h.getCompany() != null ? h.getCompany().getName() : "");
            m.put("academicYear", h.getAcademicYear() != null ? h.getAcademicYear() : "2024-2025");
            m.put("selectedCandidates", h.getSelectedCount() != null ? h.getSelectedCount() : 0);
            m.put("averagePackage", h.getAvgPackage() != null ? h.getAvgPackage() : 0.0);
            m.put("highestPackage", h.getHighestPackage() != null ? h.getHighestPackage() : 0.0);
            m.put("rolesOffered", h.getRolesOffered() != null ? h.getRolesOffered() : "Graduate Engineer");
            m.put("totalApplicants", h.getApplicantCount() != null ? h.getApplicantCount() : 0);
            m.put("totalSelected", h.getSelectedCount() != null ? h.getSelectedCount() : 0);
            result.add(m);
        }

        return result;
    }
}
package com.campusbridge.placement.service.interview;

import com.campusbridge.placement.dto.response.ConflictDetailDto;
import com.campusbridge.placement.dto.response.ConflictReportDto;
import com.campusbridge.placement.entity.Interview;
import com.campusbridge.placement.repository.InterviewRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SchedulingConflictService {

    private final InterviewRepository interviewRepository;

    public SchedulingConflictService(InterviewRepository interviewRepository) {
        this.interviewRepository = interviewRepository;
    }

    /**
     * Reusable conflict detection using half-open interval rule:
     * existingStart < requestedEnd AND existingEnd > requestedStart
     */
    public ConflictReportDto checkConflicts(Long studentId, LocalDate date, LocalTime startTime, LocalTime endTime,
                                            String mode, String venue, Long panelId, Long excludeInterviewId) {
        List<ConflictDetailDto> conflicts = new ArrayList<>();

        // 1. Student Conflict Check
        if (studentId != null) {
            List<Interview> overlapping = interviewRepository.findStudentOverlappingInterviews(
                    studentId, date, startTime, endTime, excludeInterviewId);

            for (Interview existing : overlapping) {
                String companyName = existing.getDrive() != null && existing.getDrive().getCompany() != null
                        ? existing.getDrive().getCompany().getName() : "Unknown Company";
                String roundName = existing.getStage() != null ? existing.getStage().getStageName() : "Round";

                String suggestedTime = String.format("Suggested slot: After %s or before %s on %s",
                        existing.getEndTime(), existing.getStartTime(), date);

                conflicts.add(new ConflictDetailDto(
                        "STUDENT_CONFLICT",
                        existing.getStudent() != null ? existing.getStudent().getFullName() : "Student",
                        existing.getId(),
                        companyName,
                        roundName,
                        existing.getInterviewDate(),
                        existing.getStartTime(),
                        existing.getEndTime(),
                        startTime,
                        endTime,
                        suggestedTime
                ));
            }
        }

        // 2. Physical Venue Double-Booking Check (Offline only)
        if ("OFFLINE".equalsIgnoreCase(mode) && venue != null && !venue.trim().isEmpty()) {
            List<Interview> venueOverlaps = interviewRepository.findVenueOverlappingInterviews(
                    venue.trim(), date, startTime, endTime, excludeInterviewId);

            for (Interview existing : venueOverlaps) {
                String companyName = existing.getDrive() != null && existing.getDrive().getCompany() != null
                        ? existing.getDrive().getCompany().getName() : "Unknown Company";

                conflicts.add(new ConflictDetailDto(
                        "VENUE_CONFLICT",
                        "Venue: " + venue,
                        existing.getId(),
                        companyName,
                        existing.getStage() != null ? existing.getStage().getStageName() : "Round",
                        existing.getInterviewDate(),
                        existing.getStartTime(),
                        existing.getEndTime(),
                        startTime,
                        endTime,
                        "Assign an alternative interview cabin or seminar hall."
                ));
            }
        }

        // 3. Interview Panel Conflict Check
        if (panelId != null) {
            List<Interview> panelOverlaps = interviewRepository.findPanelOverlappingInterviews(
                    panelId, date, startTime, endTime, excludeInterviewId);

            for (Interview existing : panelOverlaps) {
                conflicts.add(new ConflictDetailDto(
                        "PANEL_CONFLICT",
                        "Interview Panel #" + panelId,
                        existing.getId(),
                        existing.getDrive() != null && existing.getDrive().getCompany() != null ? existing.getDrive().getCompany().getName() : "Company",
                        existing.getStage() != null ? existing.getStage().getStageName() : "Round",
                        existing.getInterviewDate(),
                        existing.getStartTime(),
                        existing.getEndTime(),
                        startTime,
                        endTime,
                        "Assign candidates to different panels or schedule consecutively."
                ));
            }
        }

        boolean hasConflict = !conflicts.isEmpty();
        String summary = hasConflict
                ? String.format("Detected %d schedule conflict(s). Please review overlapping time slots.", conflicts.size())
                : "No scheduling conflicts detected. Slot is clear.";

        ConflictReportDto report = new ConflictReportDto(hasConflict, summary);
        report.setConflicts(conflicts);
        return report;
    }
}

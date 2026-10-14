package com.campusbridge.placement.service.recruiter;

import com.campusbridge.placement.dto.response.RecruiterDashboardStatsDto;
import com.campusbridge.placement.entity.*;
import com.campusbridge.placement.entity.enums.ApplicationStatus;
import com.campusbridge.placement.entity.enums.DriveStatus;
import com.campusbridge.placement.entity.enums.InterviewStatus;
import com.campusbridge.placement.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final InterviewerRepository interviewerRepository;
    private final InterviewPanelRepository panelRepository;
    private final PlacementDriveRepository driveRepository;
    private final ApplicationRepository applicationRepository;
    private final InterviewRepository interviewRepository;
    private final OfferRepository offerRepository;

    public CompanyService(
            CompanyRepository companyRepository,
            InterviewerRepository interviewerRepository,
            InterviewPanelRepository panelRepository,
            PlacementDriveRepository driveRepository,
            ApplicationRepository applicationRepository,
            InterviewRepository interviewRepository,
            OfferRepository offerRepository) {
        this.companyRepository = companyRepository;
        this.interviewerRepository = interviewerRepository;
        this.panelRepository = panelRepository;
        this.driveRepository = driveRepository;
        this.applicationRepository = applicationRepository;
        this.interviewRepository = interviewRepository;
        this.offerRepository = offerRepository;
    }

    public Company getCompany(Long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found: " + companyId));
    }

    @Transactional
    public Company updateCompany(Long companyId, Company updated) {
        Company existing = getCompany(companyId);
        if (updated.getName() != null) existing.setName(updated.getName());
        if (updated.getWebsite() != null) existing.setWebsite(updated.getWebsite());
        if (updated.getIndustry() != null) existing.setIndustry(updated.getIndustry());
        if (updated.getDescription() != null) existing.setDescription(updated.getDescription());
        if (updated.getAddress() != null) existing.setAddress(updated.getAddress());
        if (updated.getCity() != null) existing.setCity(updated.getCity());
        if (updated.getState() != null) existing.setState(updated.getState());
        if (updated.getHrContactName() != null) existing.setHrContactName(updated.getHrContactName());
        if (updated.getHrContactEmail() != null) existing.setHrContactEmail(updated.getHrContactEmail());
        if (updated.getHrContactPhone() != null) existing.setHrContactPhone(updated.getHrContactPhone());
        return companyRepository.save(existing);
    }

    // Interviewer & Panel management
    public List<Interviewer> getInterviewers(Long companyId) {
        return interviewerRepository.findByCompanyId(companyId);
    }

    @Transactional
    public Interviewer addInterviewer(Long companyId, Interviewer interviewer) {
        Company company = getCompany(companyId);
        interviewer.setCompany(company);
        return interviewerRepository.save(interviewer);
    }

    public List<InterviewPanel> getPanels(Long companyId) {
        return panelRepository.findByCompanyId(companyId);
    }

    @Transactional
    public InterviewPanel createPanel(Long companyId, String panelName, String description, List<Long> interviewerIds) {
        Company company = getCompany(companyId);
        InterviewPanel panel = new InterviewPanel(company, panelName, description);

        if (interviewerIds != null && !interviewerIds.isEmpty()) {
            List<Interviewer> members = interviewerRepository.findAllById(interviewerIds);
            panel.setMembers(members);
        }

        return panelRepository.save(panel);
    }

    public RecruiterDashboardStatsDto getDashboardStats(Long companyId) {
        Company company = getCompany(companyId);
        List<PlacementDrive> drives = driveRepository.findByCompanyId(companyId);
        List<Application> apps = applicationRepository.findByCompanyId(companyId);
        List<Interview> interviews = interviewRepository.findByCompanyId(companyId);
        List<Offer> offers = offerRepository.findByCompanyId(companyId);

        RecruiterDashboardStatsDto dto = new RecruiterDashboardStatsDto();
        dto.setCompanyName(company.getName());
        dto.setTotalDrives(drives.size());
        dto.setActiveDrives(drives.stream().filter(d -> d.getStatus() == DriveStatus.ACTIVE).count());
        dto.setTotalApplicants(apps.size());

        long shortlisted = apps.stream().filter(a ->
                a.getStatus() == ApplicationStatus.SHORTLISTED ||
                a.getStatus() == ApplicationStatus.ASSESSMENT ||
                a.getStatus() == ApplicationStatus.INTERVIEW ||
                a.getStatus() == ApplicationStatus.SELECTED).count();
        dto.setShortlistedCandidates(shortlisted);

        dto.setScheduledInterviews(interviews.stream().filter(i -> i.getStatus() == InterviewStatus.SCHEDULED).count());
        dto.setCompletedInterviews(interviews.stream().filter(i -> i.getStatus() == InterviewStatus.COMPLETED).count());
        dto.setTotalOffers(offers.size());

        double selectionRate = apps.size() > 0 ? ((double) offers.size() / apps.size()) * 100.0 : 0.0;
        dto.setOverallSelectionRate(Math.round(selectionRate * 10.0) / 10.0);

        return dto;
    }
}

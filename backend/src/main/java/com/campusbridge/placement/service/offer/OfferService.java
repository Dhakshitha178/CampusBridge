package com.campusbridge.placement.service.offer;

import com.campusbridge.placement.dto.request.OfferCreateRequest;
import com.campusbridge.placement.entity.*;
import com.campusbridge.placement.entity.enums.ApplicationStatus;
import com.campusbridge.placement.entity.enums.OfferStatus;
import com.campusbridge.placement.entity.enums.PlacementStatus;
import com.campusbridge.placement.repository.ApplicationRepository;
import com.campusbridge.placement.repository.OfferRepository;
import com.campusbridge.placement.repository.StudentRepository;
import com.campusbridge.placement.service.notification.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OfferService {

    private final OfferRepository offerRepository;
    private final ApplicationRepository applicationRepository;
    private final StudentRepository studentRepository;
    private final NotificationService notificationService;

    public OfferService(
            OfferRepository offerRepository,
            ApplicationRepository applicationRepository,
            StudentRepository studentRepository,
            NotificationService notificationService) {
        this.offerRepository = offerRepository;
        this.applicationRepository = applicationRepository;
        this.studentRepository = studentRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public Offer createOffer(OfferCreateRequest req) {
        Application application = applicationRepository.findById(req.getApplicationId())
                .orElseThrow(() -> new RuntimeException("Application not found: " + req.getApplicationId()));

        if (offerRepository.existsByApplicationId(application.getId())) {
            throw new RuntimeException("Offer has already been released for this candidate application.");
        }

        Student student = application.getStudent();
        PlacementDrive drive = application.getDrive();

        Offer offer = new Offer();
        offer.setApplication(application);
        offer.setStudent(student);
        offer.setCompany(drive.getCompany());
        offer.setDrive(drive);
        offer.setJobRole(req.getJobRole() != null ? req.getJobRole() : drive.getJobTitle());
        offer.setPackageLpa(req.getPackageLpa() != null ? req.getPackageLpa() : drive.getPackageLpa());
        offer.setLocation(req.getLocation() != null ? req.getLocation() : drive.getLocation());
        offer.setJoiningDate(req.getJoiningDate());
        offer.setOfferLetterUrl(req.getOfferLetterUrl());
        offer.setOfferStatus(OfferStatus.OFFERED);
        offer.setOfferedAt(LocalDateTime.now());

        Offer saved = offerRepository.save(offer);

        // Update application status to SELECTED
        application.setStatus(ApplicationStatus.SELECTED);
        applicationRepository.save(application);

        // Update student placement status to PLACED
        student.setPlacementStatus(PlacementStatus.PLACED);
        studentRepository.save(student);

        // Notify student
        if (student.getUser() != null) {
            notificationService.sendNotification(
                    student.getUser().getId(),
                    "Congratulations! Offer Letter Received",
                    String.format("You have received an employment offer from %s for '%s' (%.2f LPA).",
                            drive.getCompany().getName(), offer.getJobRole(), offer.getPackageLpa()),
                    "OFFER_RECEIVED",
                    saved.getId(),
                    "OFFER"
            );
        }

        return saved;
    }

    @Transactional
    public Offer updateOfferStatus(Long offerId, Long studentId, OfferStatus status) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found: " + offerId));

        if (!offer.getStudent().getId().equals(studentId)) {
            throw new RuntimeException("Unauthorized: Cannot respond to another student's offer.");
        }

        offer.setOfferStatus(status);
        offer.setUpdatedAt(LocalDateTime.now());
        Offer saved = offerRepository.save(offer);

        if (status == OfferStatus.REJECTED && offer.getStudent() != null) {
            // Check if student has other active accepted offers
            List<Offer> otherOffers = offerRepository.findByStudentId(studentId);
            boolean hasOtherAccepted = otherOffers.stream().anyMatch(o -> o.getOfferStatus() == OfferStatus.ACCEPTED);
            if (!hasOtherAccepted) {
                offer.getStudent().setPlacementStatus(PlacementStatus.UNPLACED);
                studentRepository.save(offer.getStudent());
            }
        }

        return saved;
    }

    public List<Offer> getStudentOffers(Long studentId) {
        return offerRepository.findByStudentId(studentId);
    }

    public List<Offer> getCompanyOffers(Long companyId) {
        return offerRepository.findByCompanyId(companyId);
    }
}

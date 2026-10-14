package com.campusbridge.placement.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailUtil {

    private static final Logger logger = LoggerFactory.getLogger(EmailUtil.class);

    public boolean sendEmail(String toEmail, String subject, String body) {
        // Email dispatcher utility with logging fallback
        logger.info("[EMAIL NOTIFICATION] Sending to: {} | Subject: {}", toEmail, subject);
        logger.debug("[EMAIL BODY]: {}", body);
        return true;
    }

    public boolean sendInterviewScheduleEmail(String toEmail, String studentName, String companyName, String roundName, String dateTime, String venueOrLink) {
        String subject = "Interview Scheduled: " + companyName + " - " + roundName;
        String body = String.format("Dear %s,\n\nYour interview for %s (%s) is scheduled on %s.\nLocation/Link: %s\n\nBest regards,\nCampusBridge Placement Cell",
                studentName, companyName, roundName, dateTime, venueOrLink);
        return sendEmail(toEmail, subject, body);
    }

    public boolean sendOfferLetterNotification(String toEmail, String studentName, String companyName, String jobRole, double ctcLpa) {
        String subject = "Congratulations! Placement Offer from " + companyName;
        String body = String.format("Dear %s,\n\nWe are pleased to inform you that you have received an offer for the role of %s at %s with a package of %.2f LPA.\n\nPlease log in to CampusBridge to review and respond to your offer.\n\nBest regards,\nCampusBridge Placement Cell",
                studentName, jobRole, companyName, ctcLpa);
        return sendEmail(toEmail, subject, body);
    }
}

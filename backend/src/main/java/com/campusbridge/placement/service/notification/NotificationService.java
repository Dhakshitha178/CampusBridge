package com.campusbridge.placement.service.notification;

import com.campusbridge.placement.entity.Notification;
import com.campusbridge.placement.entity.User;
import com.campusbridge.placement.repository.NotificationRepository;
import com.campusbridge.placement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Notification sendNotification(Long userId, String title, String message, String type, Long referenceId, String referenceType) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return null;

        Notification notification = new Notification(user, title, message, type, referenceId, referenceType);
        return notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsForUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadStatus(userId, false);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setReadStatus(true);
            notificationRepository.save(n);
        });
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> list = notificationRepository.findByUserIdAndReadStatusOrderByCreatedAtDesc(userId, false);
        for (Notification n : list) {
            n.setReadStatus(true);
        }
        notificationRepository.saveAll(list);
    }
}

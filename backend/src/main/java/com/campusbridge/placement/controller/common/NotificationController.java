package com.campusbridge.placement.controller.common;

import com.campusbridge.placement.dto.response.ApiResponse;
import com.campusbridge.placement.entity.Notification;
import com.campusbridge.placement.service.notification.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Notification>>> getNotifications(@RequestParam Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(notificationService.getNotificationsForUser(userId)));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(@RequestParam Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(notificationService.getUnreadCount(userId)));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<ApiResponse<String>> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.ok("Marked as read", "Success"));
    }

    @PostMapping("/read-all")
    public ResponseEntity<ApiResponse<String>> markAllAsRead(@RequestParam Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.ok("All marked as read", "Success"));
    }
}

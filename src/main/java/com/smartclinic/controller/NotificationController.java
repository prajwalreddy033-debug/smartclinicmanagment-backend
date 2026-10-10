package com.smartclinic.controller;

import com.smartclinic.common.ApiResponse;
import com.smartclinic.dto.notification.NotificationDto;
import com.smartclinic.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Endpoints for managing in-app notifications and alerts")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all notifications for a specific user")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getUserNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(notificationService.getUserNotifications(userId)));
    }

    @GetMapping("/user/{userId}/unread-count")
    @Operation(summary = "Get count of unread notifications for a user")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(@PathVariable Long userId) {
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("unreadCount", count)));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark a single notification as read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.ok("Notification marked as read", null));
    }

    @PatchMapping("/user/{userId}/read-all")
    @Operation(summary = "Mark all notifications for a user as read")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.ok("All notifications marked as read", null));
    }
}

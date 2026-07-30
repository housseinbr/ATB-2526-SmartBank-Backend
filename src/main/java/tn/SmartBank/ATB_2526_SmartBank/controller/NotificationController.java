package tn.SmartBank.ATB_2526_SmartBank.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.SmartBank.ATB_2526_SmartBank.dto.NotificationResponse;
import tn.SmartBank.ATB_2526_SmartBank.security.UserDetailsImpl;
import tn.SmartBank.ATB_2526_SmartBank.service.NotificationService;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('EMPLOYE', 'SUPERVISEUR', 'ADMIN')")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/me")
    public ResponseEntity<List<NotificationResponse>> myNotifications(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(
                notificationService.getAllForUser(userDetails.getId()).stream()
                        .map(NotificationResponse::fromEntity)
                        .toList()
        );
    }

    @GetMapping("/me/unread")
    public ResponseEntity<List<NotificationResponse>> myUnreadNotifications(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(
                notificationService.getUnreadForUser(userDetails.getId()).stream()
                        .map(NotificationResponse::fromEntity)
                        .toList()
        );
    }

    @GetMapping("/me/count-unread")
    public ResponseEntity<Long> countUnread(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(notificationService.countUnread(userDetails.getId()));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable Long id,
            Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(
                NotificationResponse.fromEntity(notificationService.markAsRead(id, userDetails.getId()))
        );
    }

    @PatchMapping("/me/read-all")
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        notificationService.markAllAsRead(userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}

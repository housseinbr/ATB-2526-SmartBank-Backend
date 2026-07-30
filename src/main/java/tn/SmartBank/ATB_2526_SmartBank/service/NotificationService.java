package tn.SmartBank.ATB_2526_SmartBank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Status_Notification;
import tn.SmartBank.ATB_2526_SmartBank.entity.Notification;
import tn.SmartBank.ATB_2526_SmartBank.entity.User;
import tn.SmartBank.ATB_2526_SmartBank.repository.NotificationRepository;
import tn.SmartBank.ATB_2526_SmartBank.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Notification> getAllForUser(Long userId) {
        return notificationRepository.findByUser_IdOrderByDateDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Notification> getUnreadForUser(Long userId) {
        return notificationRepository.findByUser_IdAndStatusOrderByDateDesc(userId, Status_Notification.NON_LU);
    }

    @Transactional(readOnly = true)
    public long countUnread(Long userId) {
        return notificationRepository.countByUser_IdAndStatus(userId, Status_Notification.NON_LU);
    }

    public Notification create(User recipient, String subject, String text) {
        Notification notification = Notification.builder()
                .user(recipient)
                .subject(subject)
                .text(text)
                .date(LocalDate.now())
                .status(Status_Notification.NON_LU)
                .build();
        return notificationRepository.save(notification);
    }

    public Notification createForUserId(Long userId, String subject, String text) {
        User recipient = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec l'id : " + userId));
        return create(recipient, subject, text);
    }

    public Notification markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification introuvable"));

        if (notification.getUser() == null || !notification.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Vous ne pouvez pas modifier cette notification");
        }

        notification.setStatus(Status_Notification.LU);
        return notificationRepository.save(notification);
    }

    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository.findByUser_IdAndStatusOrderByDateDesc(userId, Status_Notification.NON_LU);
        unread.forEach(notification -> notification.setStatus(Status_Notification.LU));
        notificationRepository.saveAll(unread);
    }
}

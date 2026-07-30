package tn.SmartBank.ATB_2526_SmartBank.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Status_Notification;
import tn.SmartBank.ATB_2526_SmartBank.entity.Notification;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class NotificationResponse {
    private Long id;
    private Long userId;
    private String subject;
    private LocalDate date;
    private String text;
    private Status_Notification status;
    private boolean read;

    public static NotificationResponse fromEntity(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getIdNotification())
                .userId(notification.getUser() != null ? notification.getUser().getId() : null)
                .subject(notification.getSubject())
                .date(notification.getDate())
                .text(notification.getText())
                .status(notification.getStatus())
                .read(notification.getStatus() == Status_Notification.LU)
                .build();
    }
}

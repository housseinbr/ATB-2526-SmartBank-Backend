package tn.SmartBank.ATB_2526_SmartBank.repository;

import tn.SmartBank.ATB_2526_SmartBank.entity.Notification;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Status_Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser_IdOrderByDateDesc(Long userId);

    List<Notification> findByUser_IdAndStatusOrderByDateDesc(Long userId, Status_Notification status);

    long countByUser_IdAndStatus(Long userId, Status_Notification status);
}

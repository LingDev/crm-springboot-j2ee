package com.linkdoan.backend.repository;

import com.linkdoan.backend.model.UserNotifications;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersNotificationsRepository extends JpaRepository<UserNotifications, Long> {
    Optional<UserNotifications> findFirstBySenderIdAndReceiverIdAndNotificationId(String senderId, String receiverId, Long notificationId);
}

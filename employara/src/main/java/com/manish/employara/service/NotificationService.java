package com.manish.employara.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.manish.employara.models.Notification;
import com.manish.employara.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public void notifyUser(String recipientId, Map<String, String> message, String type) {
        Notification notification = Notification.builder()
                .recipientId(recipientId)
                .message(message)
                .type(type)
                .timeStamp(LocalDateTime.now())
                .read(false)
                .build();

        notificationRepository.save(notification);

        messagingTemplate.convertAndSendToUser(
                recipientId,
                "/queue/notifications",
                notification);
    }

    public long getUnreadCount(String userId) {
        return notificationRepository.countByRecipientIdAndReadFalse(userId);
    }

    public List<Notification> getNotifications(String userId) {
        return notificationRepository.findByRecipientId(userId);
    }

    public void markAllAsRead(String userId) {
        List<Notification> notifications = notificationRepository.findByRecipientId(userId);
        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
    }

}

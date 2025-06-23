package com.manish.employara.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.manish.employara.models.Notification;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByRecipientId(String recipientId);

    long countByRecipientIdAndReadFalse(String recipientId);

    List<Notification> findAllByRecipientId(String recipientId);
}

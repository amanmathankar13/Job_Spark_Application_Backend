package com.jobportal.job_portal.Repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.jobportal.job_portal.DTO.NotificationStatus;
import com.jobportal.job_portal.Entity.Notification;


public interface NotificationRepository extends MongoRepository<Notification,Long> {
    public List<Notification> findByUserIdAndNotificationStatus(Long userId, NotificationStatus notificationStatus);

}
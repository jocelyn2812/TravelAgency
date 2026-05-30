package com.travelapp.repository;

import com.travelapp.model.Notification;
import org.springframework.data.jpa.repository
    .JpaRepository;
import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByLueFalseOrderByDateCreationDesc();

    List<Notification> findAllByOrderByDateCreationDesc();

    long countByLueFalse();
}
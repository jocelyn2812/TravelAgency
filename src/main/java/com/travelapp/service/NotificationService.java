package com.travelapp.service;

import com.travelapp.model.Notification;
import com.travelapp.model.Notification.TypeNotification;
import com.travelapp.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;

    public void creer(String message) {
        creer(message, TypeNotification.RESERVATION);
    }

    public void creer(String message,
            TypeNotification type) {
        Notification n = new Notification();
        n.setMessage(message);
        n.setType(type);
        repository.save(n);
    }

    public List<Notification> findNonLues() {
        return repository
            .findByLueFalseOrderByDateCreationDesc();
    }

    public List<Notification> findAll() {
        return repository
            .findAllByOrderByDateCreationDesc();
    }

    public long countNonLues() {
        return repository.countByLueFalse();
    }

    public void marquerLue(Long id) {
        repository.findById(id).ifPresent(n -> {
            n.setLue(true);
            repository.save(n);
        });
    }

    public void marquerToutesLues() {
        repository.findByLueFalseOrderByDateCreationDesc()
            .forEach(n -> {
                n.setLue(true);
                repository.save(n);
            });
    }
}
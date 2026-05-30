package com.travelapp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String message;

    private boolean lue = false;

    private LocalDateTime dateCreation =
        LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private TypeNotification type =
        TypeNotification.RESERVATION;

    public enum TypeNotification {
        RESERVATION, INSCRIPTION, PAIEMENT
    }
}
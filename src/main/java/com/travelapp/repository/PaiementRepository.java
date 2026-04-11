package com.travelapp.repository;

import com.travelapp.model.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaiementRepository
        extends JpaRepository<Paiement, Long> {
    List<Paiement> findByReservationId(Long reservationId);
}
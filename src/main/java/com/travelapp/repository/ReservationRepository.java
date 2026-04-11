package com.travelapp.repository;

import com.travelapp.model.Reservation;
import com.travelapp.model.Reservation.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservationRepository
        extends JpaRepository<Reservation, Long> {
    List<Reservation> findByClientId(Long clientId);
    List<Reservation> findByVoyageId(Long voyageId);
    List<Reservation> findByStatut(StatutReservation statut);
    List<Reservation> findByAgentId(Long agentId);
    long countByStatut(StatutReservation statut);
}
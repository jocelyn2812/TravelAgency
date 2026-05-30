package com.travelapp.repository;

import com.travelapp.model.Reservation;
import org.springframework.data.jpa.repository
    .JpaRepository;
import java.util.List;

public interface ReservationClientRepository
        extends JpaRepository<Reservation, Long> {

    List<Reservation> findByClientIdOrderByDateReservationDesc(
        Long clientId);
}
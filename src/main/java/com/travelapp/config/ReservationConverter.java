package com.travelapp.config;

import com.travelapp.model.Reservation;
import com.travelapp.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationConverter 
        implements Converter<String, Reservation> {

    private final ReservationRepository reservationRepository;

    @Override
    public Reservation convert(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        try {
            return reservationRepository
                .findById(Long.parseLong(id))
                .orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
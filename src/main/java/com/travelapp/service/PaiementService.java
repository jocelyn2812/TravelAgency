package com.travelapp.service;

import com.travelapp.model.Paiement;
import com.travelapp.model.Reservation;
import com.travelapp.model.Reservation.StatutReservation;
import com.travelapp.repository.PaiementRepository;
import com.travelapp.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final ReservationRepository reservationRepository;

    public List<Paiement> findAll() {
        return paiementRepository.findAll();
    }

    public List<Paiement> findByReservation(Long id) {
        return paiementRepository.findByReservationId(id);
    }

    @Transactional
    public Paiement save(Paiement paiement) {
        Paiement saved = paiementRepository.save(paiement);

        // Mettre à jour l'acompte versé sur la réservation
        if (paiement.getReservation() != null
                && paiement.getReservation().getId() != null) {

            Reservation reservation = reservationRepository
                .findById(paiement.getReservation().getId())
                .orElse(null);

            if (reservation != null) {
                // Calculer total payé
                BigDecimal totalPaye = paiementRepository
                    .findByReservationId(reservation.getId())
                    .stream()
                    .map(p -> p.getMontant() != null
                        ? p.getMontant() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                reservation.setAcompteVerse(totalPaye);

                // Si tout est payé → CONFIRMEE
                if (reservation.getMontantTotal() != null
                        && totalPaye.compareTo(
                            reservation.getMontantTotal()) >= 0) {
                    reservation.setStatut(
                        StatutReservation.CONFIRMEE);
                }

                reservationRepository.save(reservation);
            }
        }

        return saved;
    }

    public void delete(Long id) {
        paiementRepository.deleteById(id);
    }

    // Total CA
    public BigDecimal totalChiffreAffaires() {
        return paiementRepository.findAll()
            .stream()
            .map(p -> p.getMontant() != null
                ? p.getMontant() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public long countAll() {
        return paiementRepository.count();
    }
}
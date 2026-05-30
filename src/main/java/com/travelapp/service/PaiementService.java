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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final ReservationRepository reservationRepository;

    public List<Paiement> findAll() {
        return paiementRepository.findAll();
    }

    public Optional<Paiement> findById(Long id) {
        return paiementRepository.findById(id);
    }

    public List<Paiement> findByReservation(Long id) {
        return paiementRepository
            .findByReservationId(id);
    }

    @Transactional
    public Paiement save(Paiement paiement) {

        // 1 — Sauvegarder le paiement
        Paiement saved =
            paiementRepository.save(paiement);

        // 2 — Mettre à jour la réservation
        if (paiement.getReservation() != null
                && paiement.getReservation()
                   .getId() != null) {

            Reservation reservation =
                reservationRepository
                    .findById(paiement
                        .getReservation().getId())
                    .orElse(null);

            if (reservation != null) {

                // 3 — Calculer total payé
                BigDecimal totalPaye =
                    paiementRepository
                        .findByReservationId(
                            reservation.getId())
                        .stream()
                        .map(p -> p.getMontant() != null
                            ? p.getMontant()
                            : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO,
                            BigDecimal::add);

                reservation.setAcompteVerse(totalPaye);

                // 4 — Statut auto si tout payé
                if (reservation.getMontantTotal() != null
                        && totalPaye.compareTo(
                            reservation.getMontantTotal())
                           >= 0) {
                    reservation.setStatut(
                        StatutReservation.CONFIRMEE);
                }

                reservationRepository.save(reservation);
            }
        }

        return saved;
    }

    @Transactional
    public void delete(Long id) {
        // Récupérer le paiement avant suppression
        paiementRepository.findById(id).ifPresent(p -> {
            Reservation r = p.getReservation();
            paiementRepository.deleteById(id);

            // Recalculer l'acompte après suppression
            if (r != null) {
                BigDecimal totalPaye =
                    paiementRepository
                        .findByReservationId(r.getId())
                        .stream()
                        .map(pm -> pm.getMontant() != null
                            ? pm.getMontant()
                            : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO,
                            BigDecimal::add);

                r.setAcompteVerse(totalPaye);

                // Remettre EN_ATTENTE si plus assez payé
                if (r.getMontantTotal() != null
                        && totalPaye.compareTo(
                            r.getMontantTotal()) < 0
                        && r.getStatut() ==
                           StatutReservation.CONFIRMEE) {
                    r.setStatut(
                        StatutReservation.EN_ATTENTE);
                }

                reservationRepository.save(r);
            }
        });
    }

    public BigDecimal totalChiffreAffaires() {
        return paiementRepository.findAll()
            .stream()
            .map(p -> p.getMontant() != null
                ? p.getMontant()
                : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public long countAll() {
        return paiementRepository.count();
    }
}
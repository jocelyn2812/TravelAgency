package com.travelapp.service;

import com.travelapp.model.Reservation;
import com.travelapp.model.Reservation.StatutReservation;
import com.travelapp.model.Voyage;
import com.travelapp.repository.ReservationRepository;
import com.travelapp.repository.VoyageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final VoyageRepository voyageRepository;

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> findById(Long id) {
        return reservationRepository.findById(id);
    }

    @Transactional
    public Reservation save(Reservation reservation) {
        // Calcul automatique du montant total
        if (reservation.getVoyage() != null
                && reservation.getVoyage().getId() != null) {

            Voyage voyage = voyageRepository
                .findById(reservation.getVoyage().getId())
                .orElse(null);

            if (voyage != null) {
                // Calcul montant
                BigDecimal montant = BigDecimal.ZERO;

                if (reservation.getNombreAdultes() != null
                        && voyage.getPrixAdulte() != null) {
                    montant = montant.add(
                        voyage.getPrixAdulte().multiply(
                            BigDecimal.valueOf(
                                reservation.getNombreAdultes())));
                }

                if (reservation.getNombreEnfants() != null
                        && voyage.getPrixEnfant() != null) {
                    montant = montant.add(
                        voyage.getPrixEnfant().multiply(
                            BigDecimal.valueOf(
                                reservation.getNombreEnfants())));
                }

                reservation.setMontantTotal(montant);

                // Réduire les places disponibles
                // seulement pour nouvelle réservation
                if (reservation.getId() == null) {
                    int totalParticipants =
                        (reservation.getNombreAdultes() != null
                            ? reservation.getNombreAdultes() : 0)
                        + (reservation.getNombreEnfants() != null
                            ? reservation.getNombreEnfants() : 0);

                    if (voyage.getPlacesDisponibles() != null
                            && voyage.getPlacesDisponibles()
                               >= totalParticipants) {
                        voyage.setPlacesDisponibles(
                            voyage.getPlacesDisponibles()
                            - totalParticipants);
                        voyageRepository.save(voyage);
                    }
                }
            }
        }

        return reservationRepository.save(reservation);
    }

    @Transactional
    public void annuler(Long id) {
        reservationRepository.findById(id).ifPresent(r -> {
            r.setStatut(StatutReservation.ANNULEE);

            // Remettre les places disponibles
            if (r.getVoyage() != null) {
                Voyage voyage = r.getVoyage();
                int total =
                    (r.getNombreAdultes() != null
                        ? r.getNombreAdultes() : 0)
                    + (r.getNombreEnfants() != null
                        ? r.getNombreEnfants() : 0);
                voyage.setPlacesDisponibles(
                    voyage.getPlacesDisponibles() + total);
                voyageRepository.save(voyage);
            }

            reservationRepository.save(r);
        });
    }

    @Transactional
    public void confirmer(Long id) {
        reservationRepository.findById(id).ifPresent(r -> {
            r.setStatut(StatutReservation.CONFIRMEE);
            reservationRepository.save(r);
        });
    }

    @Transactional
    public void terminer(Long id) {
        reservationRepository.findById(id).ifPresent(r -> {
            r.setStatut(StatutReservation.TERMINEE);

            // Ajouter points fidélité au client
            if (r.getClient() != null
                    && r.getMontantTotal() != null) {
                int points = r.getMontantTotal()
                    .divide(BigDecimal.valueOf(10000),
                        0, java.math.RoundingMode.DOWN)
                    .intValue();
                r.getClient().setPointsFidelite(
                    r.getClient().getPointsFidelite() + points);
            }

            reservationRepository.save(r);
        });
    }

    public void delete(Long id) {
        reservationRepository.deleteById(id);
    }

    public long countAll() {
        return reservationRepository.count();
    }

    public long countByStatut(StatutReservation statut) {
        return reservationRepository
            .findByStatut(statut).size();
    }

    public List<Reservation> findByClient(Long clientId) {
        return reservationRepository
            .findByClientId(clientId);
    }

    public List<Reservation> findByStatut(
            StatutReservation statut) {
        return reservationRepository.findByStatut(statut);
    }
}
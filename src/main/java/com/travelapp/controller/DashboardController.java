package com.travelapp.controller;

import com.travelapp.model.Reservation.StatutReservation;
import com.travelapp.service.ClientService;
import com.travelapp.service.PaiementService;
import com.travelapp.service.ReservationService;
import com.travelapp.service.VoyageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final ClientService clientService;
    private final VoyageService voyageService;
    private final ReservationService reservationService;
    private final PaiementService paiementService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        // ======= STATISTIQUES GÉNÉRALES =======
        model.addAttribute("totalClients",
            clientService.countAll());

        model.addAttribute("totalVoyages",
            voyageService.countAll());

        model.addAttribute("totalReservations",
            reservationService.countAll());

        model.addAttribute("totalPaiements",
            paiementService.countAll());

        model.addAttribute("chiffreAffaires",
            paiementService.totalChiffreAffaires());

        // ======= RÉSERVATIONS PAR STATUT =======
        model.addAttribute("reservationsEnAttente",
            reservationService.countByStatut(
                StatutReservation.EN_ATTENTE));

        model.addAttribute("reservationsConfirmees",
            reservationService.countByStatut(
                StatutReservation.CONFIRMEE));

        model.addAttribute("reservationsAnnulees",
            reservationService.countByStatut(
                StatutReservation.ANNULEE));

        model.addAttribute("reservationsTerminees",
            reservationService.countByStatut(
                StatutReservation.TERMINEE));

        // ======= DERNIÈRES RÉSERVATIONS =======
        model.addAttribute("dernieresReservations",
            reservationService.findAll()
                .stream()
                .sorted((a, b) -> b.getId()
                    .compareTo(a.getId()))
                .limit(5)
                .toList());

        // ======= DERNIERS CLIENTS =======
        model.addAttribute("derniersClients",
            clientService.findAll()
                .stream()
                .sorted((a, b) -> b.getId()
                    .compareTo(a.getId()))
                .limit(5)
                .toList());


        return "dashboard/index";
    }
}
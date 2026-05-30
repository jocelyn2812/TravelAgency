package com.travelapp.controller;

import com.travelapp.model.Paiement;
import com.travelapp.model.Reservation;
import com.travelapp.service.PaiementService;
import com.travelapp.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support
    .RedirectAttributes;
import java.math.BigDecimal;

@Controller
@RequestMapping("/paiements")
@RequiredArgsConstructor
public class PaiementController {

    private final PaiementService paiementService;
    private final ReservationService reservationService;

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("paiements",
            paiementService.findAll());
        model.addAttribute("totalCA",
            paiementService.totalChiffreAffaires());
        return "paiements/liste";
    }

    @GetMapping("/nouveau")
    public String nouveau(
            Model model,
            @RequestParam(required = false)
            Long reservationId) {

        Paiement paiement = new Paiement();

        if (reservationId != null) {
            reservationService
                .findById(reservationId)
                .ifPresent(r -> {
                    paiement.setReservation(r);
                    // Montant auto = reste à payer
                    BigDecimal reste = calculerReste(r);
                    paiement.setMontant(reste);
                });
        }

        model.addAttribute("paiement", paiement);
        model.addAttribute("reservations",
            reservationService.findAll());

        // Infos réservation sélectionnée
        if (reservationId != null) {
            reservationService
                .findById(reservationId)
                .ifPresent(r -> {
                    model.addAttribute(
                        "reservationSelectionnee", r);
                    model.addAttribute(
                        "resteAPayer",
                        calculerReste(r));
                    model.addAttribute(
                        "dejaPaye",
                        r.getAcompteVerse() != null
                        ? r.getAcompteVerse()
                        : BigDecimal.ZERO);
                });
        }

        return "paiements/form";
    }

    @PostMapping("/sauvegarder")
    public String sauvegarder(
            @ModelAttribute Paiement paiement,
            RedirectAttributes redirectAttributes) {
        try {
            // Montant = reste à payer automatiquement
            if (paiement.getReservation() != null
                    && paiement.getReservation()
                       .getId() != null) {

                Reservation r = reservationService
                    .findById(paiement
                        .getReservation().getId())
                    .orElse(null);

                if (r != null) {
                    BigDecimal reste = calculerReste(r);
                    paiement.setMontant(reste);
                }
            }

            paiementService.save(paiement);
            redirectAttributes.addFlashAttribute(
                "success",
                "Paiement enregistré avec succès !");

            if (paiement.getReservation() != null
                    && paiement.getReservation()
                       .getId() != null) {
                return "redirect:/reservations/detail/"
                    + paiement.getReservation().getId();
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                "error",
                "Erreur : " + e.getMessage());
        }
        return "redirect:/paiements";
    }

    @GetMapping("/supprimer/{id}")
    public String supprimer(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        paiementService.delete(id);
        redirectAttributes.addFlashAttribute(
            "success", "Paiement supprimé !");
        return "redirect:/paiements";
    }

    // Calcul du reste à payer
    private BigDecimal calculerReste(Reservation r) {
        BigDecimal total = r.getMontantTotal() != null
            ? r.getMontantTotal()
            : BigDecimal.ZERO;
        BigDecimal paye = r.getAcompteVerse() != null
            ? r.getAcompteVerse()
            : BigDecimal.ZERO;
        BigDecimal reste = total.subtract(paye);
        return reste.compareTo(BigDecimal.ZERO) > 0
            ? reste : BigDecimal.ZERO;
    }
}
package com.travelapp.controller;

import com.travelapp.model.Paiement;
import com.travelapp.service.PaiementService;
import com.travelapp.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

        // Pré-remplir la réservation si fournie
        if (reservationId != null) {
            reservationService.findById(reservationId)
                .ifPresent(r -> {
                    paiement.setReservation(r);
                    // Pré-remplir le montant restant
                    if (r.getMontantTotal() != null) {
                        java.math.BigDecimal resteAPayer =
                            r.getMontantTotal().subtract(
                                r.getAcompteVerse() != null
                                ? r.getAcompteVerse()
                                : java.math.BigDecimal.ZERO);
                        paiement.setMontant(resteAPayer);
                    }
                });
        }

        model.addAttribute("paiement", paiement);
        model.addAttribute("reservations",
            reservationService.findAll());
        return "paiements/form";
    }

    @PostMapping("/sauvegarder")
    public String sauvegarder(
            @ModelAttribute Paiement paiement,
            RedirectAttributes redirectAttributes) {
        try {
            paiementService.save(paiement);
            redirectAttributes.addFlashAttribute(
                "success",
                "Paiement enregistré avec succès !");

            // Rediriger vers la réservation si possible
            if (paiement.getReservation() != null
                    && paiement.getReservation().getId() != null) {
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
}
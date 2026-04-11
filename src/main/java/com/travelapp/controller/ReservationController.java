package com.travelapp.controller;

import com.travelapp.model.Reservation;
import com.travelapp.service.ReservationService;
import com.travelapp.service.ClientService;
import com.travelapp.service.VoyageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final ClientService clientService;
    private final VoyageService voyageService;

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("reservations",
            reservationService.findAll());
        return "reservations/liste";
    }

    @GetMapping("/nouveau")
    public String nouveau(Model model) {
        model.addAttribute("reservation",
            new Reservation());
        model.addAttribute("clients",
            clientService.findAll());
        model.addAttribute("voyages",
            voyageService.findAll());
        return "reservations/form";
    }

    @PostMapping("/sauvegarder")
    public String sauvegarder(
            @ModelAttribute Reservation reservation,
            RedirectAttributes redirectAttributes) {
        try {
            reservationService.save(reservation);
            redirectAttributes.addFlashAttribute(
                "success",
                "Réservation sauvegardée avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                "error",
                "Erreur : " + e.getMessage());
        }
        return "redirect:/reservations";
    }

    @GetMapping("/modifier/{id}")
    public String modifier(
            @PathVariable Long id, Model model) {
        reservationService.findById(id).ifPresent(
            r -> model.addAttribute("reservation", r));
        model.addAttribute("clients",
            clientService.findAll());
        model.addAttribute("voyages",
            voyageService.findAll());
        return "reservations/form";
    }

    @GetMapping("/detail/{id}")
    public String detail(
            @PathVariable Long id, Model model) {
        reservationService.findById(id).ifPresent(r -> {
            model.addAttribute("reservation", r);
        });
        return "reservations/detail";
    }

    @GetMapping("/confirmer/{id}")
    public String confirmer(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        reservationService.confirmer(id);
        redirectAttributes.addFlashAttribute(
            "success", "Réservation confirmée !");
        return "redirect:/reservations";
    }

    @GetMapping("/annuler/{id}")
    public String annuler(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        reservationService.annuler(id);
        redirectAttributes.addFlashAttribute(
            "success", "Réservation annulée !");
        return "redirect:/reservations";
    }

    @GetMapping("/terminer/{id}")
    public String terminer(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        reservationService.terminer(id);
        redirectAttributes.addFlashAttribute(
            "success",
            "Voyage terminé ! Points fidélité ajoutés.");
        return "redirect:/reservations";
    }

    @GetMapping("/supprimer/{id}")
    public String supprimer(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        reservationService.delete(id);
        redirectAttributes.addFlashAttribute(
            "success", "Réservation supprimée !");
        return "redirect:/reservations";
    }
}
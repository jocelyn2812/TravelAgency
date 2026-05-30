package com.travelapp.controller;

import com.travelapp.model.Voyage;
import com.travelapp.service.DestinationService;
import com.travelapp.service.VoyageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support
    .RedirectAttributes;

@Controller
@RequestMapping("/voyages")
@RequiredArgsConstructor
public class VoyageController {

    private final VoyageService voyageService;
    private final DestinationService destinationService;

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("voyages",
            voyageService.findAll());
        model.addAttribute("totalActifs",
            voyageService.countActifs());
        return "voyages/liste";
    }

    @GetMapping("/nouveau")
    public String nouveau(Model model) {
        model.addAttribute("voyage", new Voyage());
        model.addAttribute("destinations",
            destinationService.findAll());
        return "voyages/form";
    }

    @PostMapping("/sauvegarder")
    public String sauvegarder(
            @ModelAttribute Voyage voyage,
            RedirectAttributes redirectAttributes) {
        try {
            // Places disponibles = places total
            // si nouveau voyage
            if (voyage.getId() == null
                    && voyage.getPlacesDisponibles()
                       == null
                    && voyage.getPlacesTotal() != null) {
                voyage.setPlacesDisponibles(
                    voyage.getPlacesTotal());
            }
            voyageService.save(voyage);
            redirectAttributes.addFlashAttribute(
                "success",
                "Voyage sauvegardé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                "error",
                "Erreur : " + e.getMessage());
        }
        return "redirect:/voyages";
    }

    @GetMapping("/modifier/{id}")
    public String modifier(
            @PathVariable Long id, Model model) {
        voyageService.findById(id).ifPresent(
            v -> model.addAttribute("voyage", v));
        model.addAttribute("destinations",
            destinationService.findAll());
        return "voyages/form";
    }

    @GetMapping("/activer/{id}")
    public String activer(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        voyageService.findById(id).ifPresent(v -> {
            v.setActif(true);
            voyageService.save(v);
        });
        redirectAttributes.addFlashAttribute(
            "success", "Voyage activé !");
        return "redirect:/voyages";
    }

    @GetMapping("/desactiver/{id}")
    public String desactiver(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        voyageService.findById(id).ifPresent(v -> {
            v.setActif(false);
            voyageService.save(v);
        });
        redirectAttributes.addFlashAttribute(
            "success", "Voyage désactivé !");
        return "redirect:/voyages";
    }

    @GetMapping("/supprimer/{id}")
    public String supprimer(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        voyageService.delete(id);
        redirectAttributes.addFlashAttribute(
            "success", "Voyage supprimé !");
        return "redirect:/voyages";
    }
}
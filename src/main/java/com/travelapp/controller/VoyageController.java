package com.travelapp.controller;

import com.travelapp.model.Voyage;
import com.travelapp.service.VoyageService;
import com.travelapp.service.DestinationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
            @ModelAttribute Voyage voyage) {
        voyageService.save(voyage);
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

    @GetMapping("/supprimer/{id}")
    public String supprimer(@PathVariable Long id) {
        voyageService.delete(id);
        return "redirect:/voyages";
    }
}
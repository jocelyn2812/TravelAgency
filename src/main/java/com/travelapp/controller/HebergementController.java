package com.travelapp.controller;

import com.travelapp.model.Hebergement;
import com.travelapp.service.HebergementService;
import com.travelapp.service.DestinationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/hebergements")
@RequiredArgsConstructor
public class HebergementController {

    private final HebergementService hebergementService;
    private final DestinationService destinationService;

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("hebergements",
            hebergementService.findAll());
        return "hebergements/liste";
    }

    @GetMapping("/nouveau")
    public String nouveau(Model model) {
        model.addAttribute("hebergement",
            new Hebergement());
        model.addAttribute("destinations",
            destinationService.findAll());
        return "hebergements/form";
    }

    @PostMapping("/sauvegarder")
    public String sauvegarder(
            @ModelAttribute Hebergement hebergement) {
        hebergementService.save(hebergement);
        return "redirect:/hebergements";
    }

    @GetMapping("/modifier/{id}")
    public String modifier(
            @PathVariable Long id, Model model) {
        hebergementService.findById(id).ifPresent(
            h -> model.addAttribute("hebergement", h));
        model.addAttribute("destinations",
            destinationService.findAll());
        return "hebergements/form";
    }

    @GetMapping("/supprimer/{id}")
    public String supprimer(@PathVariable Long id) {
        hebergementService.delete(id);
        return "redirect:/hebergements";
    }
}
package com.travelapp.controller;

import com.travelapp.model.Destination;
import com.travelapp.service.DestinationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/destinations")
@RequiredArgsConstructor
public class DestinationController {

    private final DestinationService destinationService;

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("destinations",
            destinationService.findAll());
        return "destinations/liste";
    }

    @GetMapping("/nouveau")
    public String nouveau(Model model) {
        model.addAttribute("destination",
            new Destination());
        return "destinations/form";
    }

    @PostMapping("/sauvegarder")
    public String sauvegarder(
            @ModelAttribute Destination destination) {
        destinationService.save(destination);
        return "redirect:/destinations";
    }

    @GetMapping("/modifier/{id}")
    public String modifier(
            @PathVariable Long id, Model model) {
        destinationService.findById(id).ifPresent(
            d -> model.addAttribute("destination", d));
        return "destinations/form";
    }

    @GetMapping("/supprimer/{id}")
    public String supprimer(@PathVariable Long id) {
        destinationService.delete(id);
        return "redirect:/destinations";
    }
}
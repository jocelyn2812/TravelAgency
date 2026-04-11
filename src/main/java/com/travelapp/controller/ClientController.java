package com.travelapp.controller;

import com.travelapp.model.Client;
import com.travelapp.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    public String liste(Model model,
            @RequestParam(required = false) String search) {
        if (search != null && !search.isEmpty()) {
            model.addAttribute("clients",
                clientService.rechercher(search));
        } else {
            model.addAttribute("clients",
                clientService.findAll());
        }
        model.addAttribute("search", search);
        return "clients/liste";
    }

    @GetMapping("/nouveau")
    public String nouveau(Model model) {
        model.addAttribute("client", new Client());
        return "clients/form";
    }

    @PostMapping("/sauvegarder")
    public String sauvegarder(
            @Valid @ModelAttribute Client client,
            BindingResult result) {
        if (result.hasErrors()) return "clients/form";
        clientService.save(client);
        return "redirect:/clients";
    }

    @GetMapping("/modifier/{id}")
    public String modifier(
            @PathVariable Long id, Model model) {
        clientService.findById(id).ifPresent(
            c -> model.addAttribute("client", c));
        return "clients/form";
    }

    @GetMapping("/supprimer/{id}")
    public String supprimer(@PathVariable Long id) {
        clientService.delete(id);
        return "redirect:/clients";
    }

    @GetMapping("/detail/{id}")
    public String detail(
            @PathVariable Long id, Model model) {
        clientService.findById(id).ifPresent(
            c -> model.addAttribute("client", c));
        return "clients/detail";
    }
}
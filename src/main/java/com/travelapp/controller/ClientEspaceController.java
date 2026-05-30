package com.travelapp.controller;

import com.travelapp.model.Client;
import com.travelapp.model.ClientCompte;
import com.travelapp.model.Notification.TypeNotification;
import com.travelapp.model.Reservation;
import com.travelapp.model.Voyage;
import com.travelapp.repository.ClientRepository;
import com.travelapp.service.ClientCompteService;
import com.travelapp.service.NotificationService;
import com.travelapp.service.ReservationService;
import com.travelapp.service.VoyageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation
    .AuthenticationPrincipal;
import org.springframework.security.core.userdetails
    .UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support
    .RedirectAttributes;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientEspaceController {

    private final VoyageService voyageService;
    private final ClientCompteService clientService;
    private final ReservationService reservationService;
    private final ClientRepository clientRepository;
    private final NotificationService notificationService;

    // ===== ACCUEIL =====
    @GetMapping({"", "/", "/accueil"})
    public String accueil(Model model) {
        model.addAttribute("voyages",
            voyageService.findActifsAvecPlaces()
                .stream().limit(6).toList());
        model.addAttribute("totalVoyages",
            voyageService.countActifs());
        return "client/accueil";
    }

    // ===== CATALOGUE =====
    @GetMapping("/voyages")
    public String voyages(
            Model model,
            @RequestParam(required = false)
            String destination,
            @RequestParam(required = false)
            String type,
            @RequestParam(required = false)
            String saison) {

        List<Voyage> voyages =
            voyageService.findActifsAvecPlaces();

        if (destination != null
                && !destination.isBlank()) {
            String dest = destination.toLowerCase();
            voyages = voyages.stream()
                .filter(v ->
                    v.getDestination() != null
                    && (v.getDestination().getVille()
                        .toLowerCase().contains(dest)
                    || v.getDestination().getPays()
                        .toLowerCase().contains(dest)))
                .toList();
        }

        if (type != null && !type.isBlank()) {
            voyages = voyages.stream()
                .filter(v -> v.getType() != null
                    && v.getType().name().equals(type))
                .toList();
        }

        if (saison != null && !saison.isBlank()) {
            voyages = voyages.stream()
                .filter(v -> v.getSaison() != null
                    && v.getSaison().name()
                        .equals(saison))
                .toList();
        }

        model.addAttribute("voyages", voyages);
        model.addAttribute("destination",
            destination);
        model.addAttribute("type", type);
        model.addAttribute("saison", saison);
        return "client/voyages";
    }

    // ===== DÉTAIL VOYAGE =====
    @GetMapping("/voyages/{id}")
    public String detailVoyage(
            @PathVariable Long id, Model model) {
        voyageService.findById(id).ifPresent(
            v -> model.addAttribute("voyage", v));
        return "client/voyage-detail";
    }

    // ===== INSCRIPTION =====
    @GetMapping("/inscription")
    public String inscriptionForm(Model model) {
        model.addAttribute("client",
            new ClientCompte());
        return "client/inscription";
    }

    @PostMapping("/inscription")
    public String inscrire(
            @Valid @ModelAttribute("client")
            ClientCompte client,
            BindingResult result,
            @RequestParam String confirmPassword,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!client.getPassword()
                .equals(confirmPassword)) {
            result.rejectValue("password",
                "error.password",
                "Les mots de passe ne "
                + "correspondent pas !");
        }

        if (client.getPassword() != null
                && !client.getPassword().isEmpty()) {
            boolean hasMaj = client.getPassword()
                .chars()
                .anyMatch(Character::isUpperCase);
            boolean hasChiffre = client.getPassword()
                .chars()
                .anyMatch(Character::isDigit);
            if (!hasMaj || !hasChiffre
                    || client.getPassword()
                       .length() < 8) {
                result.rejectValue("password",
                    "error.password",
                    "8+ caractères, 1 majuscule "
                    + "et 1 chiffre minimum");
            }
        }

        if (!result.hasFieldErrors("email")
                && clientService.findByEmail(
                    client.getEmail()).isPresent()) {
            result.rejectValue("email",
                "error.email",
                "Cet email est déjà utilisé !");
        }

        if (result.hasErrors()) {
            return "client/inscription";
        }

        try {
            ClientCompte saved =
                clientService.inscrire(
                    client, confirmPassword);

            // ✅ Créer aussi dans table clients
            if (!clientRepository.existsByEmail(
                    saved.getEmail())) {
                Client clientAdmin = new Client();
                clientAdmin.setNom(saved.getNom());
                clientAdmin.setPrenom(
                    saved.getPrenom());
                clientAdmin.setEmail(
                    saved.getEmail());
                clientAdmin.setTelephone(
                    saved.getTelephone());
                clientAdmin.setPointsFidelite(0);
                clientRepository.save(clientAdmin);
            }

            // ✅ Notification admin
            notificationService.creer(
                "Nouveau client inscrit : "
                + saved.getNom()
                + " "
                + saved.getPrenom()
                + " ("
                + saved.getEmail()
                + ")",
                TypeNotification.INSCRIPTION);

            redirectAttributes.addFlashAttribute(
                "success",
                "Compte créé avec succès ! "
                + "Connectez-vous maintenant.");
            return "redirect:/client/connexion";

        } catch (Exception e) {
            model.addAttribute("error",
                e.getMessage());
            return "client/inscription";
        }
    }

    // ===== CONNEXION =====
    @GetMapping("/connexion")
    public String connexion() {
        return "client/connexion";
    }

    // ===== RÉSERVATION =====
    @GetMapping("/reserver/{voyageId}")
    public String reserverForm(
            @PathVariable Long voyageId,
            Model model,
            @AuthenticationPrincipal
            UserDetails userDetails) {

        if (userDetails == null) {
            return "redirect:/client/connexion";
        }

        Voyage voyage = voyageService
            .findById(voyageId).orElse(null);

        if (voyage == null
                || voyage.getPlacesDisponibles()
                   <= 0) {
            return "redirect:/client/voyages";
        }

        model.addAttribute("voyage", voyage);
        return "client/reservation-form";
    }

    @PostMapping("/reserver/{voyageId}")
    public String reserver(
            @PathVariable Long voyageId,
            @RequestParam int nombreAdultes,
            @RequestParam(defaultValue = "0")
            int nombreEnfants,
            @RequestParam(required = false)
            String observations,
            @AuthenticationPrincipal
            UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        if (nombreAdultes < 1) {
            redirectAttributes.addFlashAttribute(
                "error",
                "Au moins 1 adulte requis !");
            return "redirect:/client/reserver/"
                + voyageId;
        }

        try {
            ClientCompte compte = clientService
                .findByEmail(
                    userDetails.getUsername())
                .orElseThrow();

            // ✅ Récupérer ou créer client en base
            Client client = clientRepository
                .findByEmail(compte.getEmail())
                .orElseGet(() -> {
                    Client nouveau = new Client();
                    nouveau.setNom(compte.getNom());
                    nouveau.setPrenom(
                        compte.getPrenom());
                    nouveau.setEmail(
                        compte.getEmail());
                    nouveau.setTelephone(
                        compte.getTelephone());
                    nouveau.setNationalite(
                        compte.getNationalite());
                    nouveau.setPointsFidelite(
                        compte.getPointsFidelite());
                    return clientRepository
                        .save(nouveau);
                });

            Voyage voyage = voyageService
                .findById(voyageId)
                .orElseThrow(() ->
                    new RuntimeException(
                        "Voyage introuvable !"));

            int total = nombreAdultes + nombreEnfants;
            if (total > voyage
                    .getPlacesDisponibles()) {
                throw new RuntimeException(
                    "Places insuffisantes ! "
                    + "Seulement "
                    + voyage.getPlacesDisponibles()
                    + " place(s) disponible(s).");
            }

            Reservation reservation =
                new Reservation();
            reservation.setClient(client);
            reservation.setVoyage(voyage);
            reservation.setNombreAdultes(
                nombreAdultes);
            reservation.setNombreEnfants(
                nombreEnfants);
            reservation.setObservations(
                observations);
            reservation.setDateReservation(
                LocalDate.now());

            reservationService.save(reservation);

            // ✅ Notification admin
            notificationService.creer(
                "🗓️ Nouvelle réservation de "
                + client.getNom()
                + " "
                + client.getPrenom()
                + " pour le voyage : "
                + voyage.getTitre(),
                TypeNotification.RESERVATION);

            redirectAttributes.addFlashAttribute(
                "success",
                "✅ Réservation créée ! "
                + "Notre équipe vous "
                + "contactera sous 24h.");

            return "redirect:/client/"
                + "mes-reservations";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                "error", e.getMessage());
            return "redirect:/client/reserver/"
                + voyageId;
        }
    }

    // ===== MES RÉSERVATIONS =====
    @GetMapping("/mes-reservations")
    public String mesReservations(
            Model model,
            @AuthenticationPrincipal
            UserDetails userDetails) {

        ClientCompte compte = clientService
            .findByEmail(userDetails.getUsername())
            .orElseThrow();

        // ✅ Chercher dans table clients par email
        List<Reservation> reservations =
            clientRepository
                .findByEmail(compte.getEmail())
                .map(c -> reservationService
                    .findAll().stream()
                    .filter(r ->
                        r.getClient() != null
                        && r.getClient().getId()
                            .equals(c.getId()))
                    .sorted((a, b) ->
                        b.getDateReservation()
                         .compareTo(
                            a.getDateReservation()))
                    .toList())
                .orElse(List.of());

        model.addAttribute("compte", compte);
        model.addAttribute("reservations",
            reservations);
        return "client/mes-reservations";
    }

    // ===== MON PROFIL =====
    @GetMapping("/profil")
    public String profil(
            Model model,
            @AuthenticationPrincipal
            UserDetails userDetails) {

        clientService.findByEmail(
            userDetails.getUsername())
            .ifPresent(c ->
                model.addAttribute("compte", c));
        return "client/profil";
    }

    @PostMapping("/profil/modifier")
    public String modifierProfil(
            @ModelAttribute ClientCompte profil,
            @AuthenticationPrincipal
            UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        try {
            ClientCompte compte = clientService
                .findByEmail(
                    userDetails.getUsername())
                .orElseThrow();

            compte.setNom(profil.getNom());
            compte.setPrenom(profil.getPrenom());
            compte.setTelephone(
                profil.getTelephone());
            compte.setAdresse(profil.getAdresse());
            compte.setVille(profil.getVille());
            compte.setPays(profil.getPays());
            compte.setNationalite(
                profil.getNationalite());
            compte.setNumeroPasseport(
                profil.getNumeroPasseport());
            compte.setDateNaissance(
                profil.getDateNaissance());
            compte.setPreferences(
                profil.getPreferences());

            clientService.save(compte);

            // ✅ Mettre à jour aussi dans table clients
            clientRepository
                .findByEmail(compte.getEmail())
                .ifPresent(c -> {
                    c.setNom(profil.getNom());
                    c.setPrenom(profil.getPrenom());
                    c.setTelephone(
                        profil.getTelephone());
                    c.setNationalite(
                        profil.getNationalite());
                    c.setNumeroPasseport(
                        profil.getNumeroPasseport());
                    clientRepository.save(c);
                });

            redirectAttributes.addFlashAttribute(
                "success",
                "Profil mis à jour !");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                "error", e.getMessage());
        }

        return "redirect:/client/profil";
    }
}
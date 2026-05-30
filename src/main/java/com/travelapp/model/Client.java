package com.travelapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Pattern(
        regexp = "^[a-zA-ZÀ-ÿ\\s\\-']{2,50}$",
        message = "Le nom doit contenir "
            + "uniquement des lettres")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Pattern(
        regexp = "^[a-zA-ZÀ-ÿ\\s\\-']{2,50}$",
        message = "Le prénom doit contenir "
            + "uniquement des lettres")
    private String prenom;

    @Email(message = "Format email invalide")
    @Column(unique = true)
    private String email;

    @Pattern(
        regexp = "^[+]?[0-9\\s]{8,15}$",
        message = "Numéro invalide "
            + "(chiffres uniquement)")
    private String telephone;

    private String adresse;

    @Pattern(
        regexp = "^[a-zA-ZÀ-ÿ\\s\\-']{0,100}$",
        message = "La ville doit contenir "
            + "uniquement des lettres")
    private String ville;

    @Pattern(
        regexp = "^[a-zA-ZÀ-ÿ\\s\\-']{0,100}$",
        message = "Le pays doit contenir "
            + "uniquement des lettres")
    private String pays;

    @Past(message = "Date de naissance invalide")
    private LocalDate dateNaissance;

    @Pattern(
        regexp = "^[A-Z0-9]{0,20}$",
        message = "Numéro de passeport invalide")
    private String numeroPasseport;

    @Future(message = "Date d'expiration invalide")
    private LocalDate expirationPasseport;

    @Pattern(
        regexp = "^[a-zA-ZÀ-ÿ\\s\\-']{0,100}$",
        message = "La nationalité doit contenir "
            + "uniquement des lettres")
    private String nationalite;

    private Integer pointsFidelite = 0;

    @Column(columnDefinition = "TEXT")
    private String preferences;

    private LocalDate dateInscription =
        LocalDate.now();

    @OneToMany(mappedBy = "client",
               cascade = CascadeType.ALL,
               fetch = FetchType.LAZY)
    private List<Reservation> reservations;
}
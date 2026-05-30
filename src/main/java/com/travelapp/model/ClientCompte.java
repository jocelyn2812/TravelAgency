package com.travelapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "clients_comptes")
public class ClientCompte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Pattern(
        regexp = "^[a-zA-ZÀ-ÿ\\s\\-']{2,50}$",
        message = "Le nom ne doit contenir "
            + "que des lettres (min 2, max 50)")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Pattern(
        regexp = "^[a-zA-ZÀ-ÿ\\s\\-']{2,50}$",
        message = "Le prénom ne doit contenir "
            + "que des lettres (min 2, max 50)")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format email invalide")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(
        min = 8,
        message = "Le mot de passe doit contenir "
            + "au moins 8 caractères")
    private String password;

    @Pattern(
        regexp = "^[+]?[0-9\\s]{8,15}$",
        message = "Numéro de téléphone invalide "
            + "(chiffres uniquement, 8-15 chiffres)")
    private String telephone;

    @Size(max = 200,
        message = "L'adresse est trop longue")
    private String adresse;

    @Pattern(
        regexp = "^[a-zA-ZÀ-ÿ\\s\\-']{0,100}$",
        message = "La ville ne doit contenir "
            + "que des lettres")
    private String ville;

    @Pattern(
        regexp = "^[a-zA-ZÀ-ÿ\\s\\-']{0,100}$",
        message = "Le pays ne doit contenir "
            + "que des lettres")
    private String pays;

    @Past(message = "La date de naissance "
        + "doit être dans le passé")
    private LocalDate dateNaissance;

    @Pattern(
        regexp = "^[a-zA-ZÀ-ÿ\\s\\-']{0,100}$",
        message = "La nationalité ne doit contenir "
            + "que des lettres")
    private String nationalite;

    @Pattern(
        regexp = "^[A-Z0-9]{0,20}$",
        message = "Numéro de passeport invalide "
            + "(lettres majuscules et chiffres)")
    private String numeroPasseport;

    @Future(message = "La date d'expiration "
        + "du passeport doit être dans le futur")
    private LocalDate expirationPasseport;

    private Integer pointsFidelite = 0;

    @Size(max = 500,
        message = "Les préférences sont trop longues")
    @Column(columnDefinition = "TEXT")
    private String preferences;

    private boolean actif = true;

    private LocalDateTime dateInscription =
        LocalDateTime.now();
}
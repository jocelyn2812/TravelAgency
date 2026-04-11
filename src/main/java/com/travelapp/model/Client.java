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
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @Email(message = "Email invalide")
    @Column(unique = true)
    private String email;

    private String telephone;
    private String adresse;
    private String ville;
    private String pays;
    private LocalDate dateNaissance;
    private String numeroPasseport;
    private LocalDate expirationPasseport;
    private String nationalite;
    private Integer pointsFidelite = 0;

    @Column(columnDefinition = "TEXT")
    private String preferences;

    private LocalDate dateInscription = LocalDate.now();

    @OneToMany(mappedBy = "client",
               cascade = CascadeType.ALL,
               fetch = FetchType.LAZY)
    private List<Reservation> reservations;
}
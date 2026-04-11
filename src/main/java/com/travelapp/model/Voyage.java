package com.travelapp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "voyages")
public class Voyage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "destination_id")
    private Destination destination;

    private LocalDate dateDepart;
    private LocalDate dateRetour;
    private BigDecimal prixAdulte;
    private BigDecimal prixEnfant;
    private Integer placesTotal;
    private Integer placesDisponibles;

    @Enumerated(EnumType.STRING)
    private TypeVoyage type;

    @Enumerated(EnumType.STRING)
    private Saison saison;

    private boolean actif = true;

    @OneToMany(mappedBy = "voyage",
               cascade = CascadeType.ALL,
               fetch = FetchType.LAZY)
    private List<Reservation> reservations;

    public enum TypeVoyage {
        TOUT_INCLUS, DEMI_PENSION, CIRCUIT, SEJOUR
    }

    public enum Saison {
        HAUTE, BASSE, MOYENNE
    }
}
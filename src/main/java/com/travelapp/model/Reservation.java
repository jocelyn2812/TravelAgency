package com.travelapp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "voyage_id")
    private Voyage voyage;

    private LocalDate dateReservation = LocalDate.now();
    private Integer nombreAdultes;
    private Integer nombreEnfants;
    private BigDecimal montantTotal;
    private BigDecimal acompteVerse;

    @Enumerated(EnumType.STRING)
    private StatutReservation statut = StatutReservation.EN_ATTENTE;

    @Column(columnDefinition = "TEXT")
    private String observations;

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private Utilisateur agent;

    @OneToMany(mappedBy = "reservation",
               cascade = CascadeType.ALL,
               fetch = FetchType.LAZY)
    private List<Paiement> paiements;

    public enum StatutReservation {
        EN_ATTENTE, CONFIRMEE, ANNULEE, TERMINEE
    }
}
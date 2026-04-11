package com.travelapp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "paiements")
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    private BigDecimal montant;
    private LocalDate datePaiement = LocalDate.now();

    @Enumerated(EnumType.STRING)
    private ModePaiement mode;

    @Enumerated(EnumType.STRING)
    private TypePaiement type;

    private String reference;

    public enum ModePaiement {
        ESPECES, VIREMENT, CARTE, CHEQUE
    }

    public enum TypePaiement {
        ACOMPTE, SOLDE, REMBOURSEMENT
    }
}
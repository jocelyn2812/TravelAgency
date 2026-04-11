package com.travelapp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "hebergements")
public class Hebergement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String type;

    @ManyToOne
    @JoinColumn(name = "destination_id")
    private Destination destination;

    private Integer etoiles;

    @Column(columnDefinition = "TEXT")
    private String equipements;

    private BigDecimal prixParNuit;
    private String photo;
    private boolean disponible = true;
}
package com.travelapp.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "destinations")
public class Destination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pays;
    private String ville;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String photo;
    private String continent;
    private boolean actif = true;
}
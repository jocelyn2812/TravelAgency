package com.travelapp.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private NomRole nom;

    public enum NomRole {
        ROLE_ADMIN,
        ROLE_COMMERCIAL,
        ROLE_CAISSIER
    }
}
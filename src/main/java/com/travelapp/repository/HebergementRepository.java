package com.travelapp.repository;

import com.travelapp.model.Hebergement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HebergementRepository
        extends JpaRepository<Hebergement, Long> {
    List<Hebergement> findByDisponibleTrue();
}
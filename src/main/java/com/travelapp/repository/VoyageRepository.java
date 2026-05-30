package com.travelapp.repository;

import com.travelapp.model.Voyage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface VoyageRepository
        extends JpaRepository<Voyage, Long> {

    // Voyages actifs
    List<Voyage> findByActifTrue();

    // Voyages avec places disponibles
    List<Voyage> findByActifTrueAndPlacesDisponiblesGreaterThan(
        int places);

    // Voyages par destination
    List<Voyage> findByDestinationId(Long destinationId);

    // Recherche par titre
    List<Voyage> findByTitreContainingIgnoreCase(
        String titre);

    // Count actifs
    long countByActifTrue();
}
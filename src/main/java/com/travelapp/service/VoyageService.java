package com.travelapp.service;

import com.travelapp.model.Voyage;
import com.travelapp.repository.VoyageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoyageService {

    private final VoyageRepository voyageRepository;

    public List<Voyage> findAll() {
        return voyageRepository.findAll();
    }

    // ✅ Voyages actifs
    public List<Voyage> findActifs() {
        return voyageRepository.findByActifTrue();
    }

    // ✅ Voyages actifs avec places disponibles
    public List<Voyage> findActifsAvecPlaces() {
        return voyageRepository
            .findByActifTrueAndPlacesDisponiblesGreaterThan(0);
    }

    public Optional<Voyage> findById(Long id) {
        return voyageRepository.findById(id);
    }

    public Voyage save(Voyage voyage) {
        return voyageRepository.save(voyage);
    }

    public void delete(Long id) {
        voyageRepository.deleteById(id);
    }

    public long countAll() {
        return voyageRepository.count();
    }

    public long countActifs() {
        return voyageRepository.countByActifTrue();
    }
}
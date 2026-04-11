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

    public Object findActifs() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}